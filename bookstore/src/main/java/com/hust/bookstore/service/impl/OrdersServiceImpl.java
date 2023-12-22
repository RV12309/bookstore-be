package com.hust.bookstore.service.impl;

import com.hust.bookstore.dto.OrderItemDto;
import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.OrderRequest;
import com.hust.bookstore.dto.request.OrderStatusRequest;
import com.hust.bookstore.dto.request.PaymentStatusRequest;
import com.hust.bookstore.dto.request.SearchOrderRequest;
import com.hust.bookstore.dto.response.OrderResponse;
import com.hust.bookstore.entity.*;
import com.hust.bookstore.enumration.*;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.NotificationService;
import com.hust.bookstore.service.OrdersService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.hust.bookstore.enumration.PaymentStatus.PENDING;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
public class OrdersServiceImpl extends BusinessHelper implements OrdersService {

    public OrdersServiceImpl(BookRepository bookRepository, CartRepository cartRepository,
                             CartItemRepository cartItemRepository, PaymentRepository paymentRepository,
                             DeliveryPartnerConfigRepository deliveryPartnerConfigRepo,
                             StoreDeliveryPartnerRepository storeDeliveryPartnerRepo, UserRepository userRepository,
                             OrderDetailRepository orderDetailsRepository, OrderItemsRepository orderItemsRepository,
                             CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository,
                             AccountRepository accountRepository, AuthService authService,
                             BookImageRepository bookImageRepository, ModelMapper modelMapper,
                             NotificationService notificationService, UserAddressRepository addressRepository) {
        super(bookRepository, cartRepository, cartItemRepository, paymentRepository,
                deliveryPartnerConfigRepo, storeDeliveryPartnerRepo, userRepository,
                orderDetailsRepository, orderItemsRepository, categoryRepository,
                bookCategoryRepository, accountRepository, authService, bookImageRepository,
                modelMapper, notificationService, addressRepository);
    }

    @Override
    public List<OrderResponse> createOrder(OrderRequest request) {
        log.info("Start create order");
        ShoppingCart cart = cartRepository.findById(request.getSessionId()).orElse(null);
        if (cart == null) {
            log.info("Cart not found, throw exception");
            throw new BusinessException(ResponseCode.CART_NOT_FOUND);
        }
        List<CartItem> cartItems = cartItemRepository.findAllBySessionId(cart.getId());
        if (CollectionUtils.isEmpty(cartItems)) {
            log.info("Cart is empty, throw exception");
            throw new BusinessException(ResponseCode.CART_ITEM_NOT_FOUND);
        }

        Account currentAccount = authService.getCurrentAccountLogin();
        User savedUser;
        if (nonNull(currentAccount)) {
            log.info("Current account existed, get user by account id");
            Long userId = currentAccount.getUserId();
            if (isNull(userId)) {
                log.info("User not existed, create new user");
                User user = User.builder()
                        .email(request.getEmail())
                        .name(request.getName())
                        .phone(request.getPhone())
                        .gender(Gender.OTHER)
                        .accountId(currentAccount.getId())
                        .build();
                savedUser = userRepository.save(user);
            } else {
                log.info("User existed, get user by user id");
                savedUser = userRepository.findById(userId).orElse(null);
            }
        } else {
            log.info("Current account not existed, create new user");
            User user = User.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .phone(request.getPhone())
                    .gender(Gender.OTHER)
                    .build();
            savedUser = userRepository.save(user);
        }

        LocalDateTime now = LocalDateTime.now();

        List<OrderDetails> savedOrders = saveOrderDetail(savedUser, cart, cartItems, now);
        List<OrderItems> savedItems = createOrderItems(cartItems, savedOrders);
        List<PaymentDetails> payment = createPayment(request, savedUser, savedOrders);

        updateBooksQuantity(cartItems);

        log.info("Start delete cart item");
        cartItemRepository.deleteAll(cartItems);
        log.info("End delete cart item");

        log.info("Start delete cart");
        cartRepository.delete(cart);
        log.info("End delete cart");

        log.info("Start create order response");
        //create map response by order id
        Map<Long, OrderDetails> orderMap = savedOrders.stream().collect(toMap(OrderDetails::getId, Function.identity()));
        //create map response by order id
        Map<Long, PaymentDetails> paymentMap = payment.stream().collect(toMap(PaymentDetails::getOrderId, Function.identity()));
        Map<Long, List<OrderItems>> orderItemMap = savedItems.stream().collect(groupingBy(OrderItems::getOrderId));
        List<Long> bookIds = savedItems.stream().map(OrderItems::getBookId).toList();
        List<Book> books = bookRepository.findAllById(bookIds);
        Map<Long, Book> bookMap = books.stream().collect(toMap(Book::getId, Function.identity()));
        //create response
        List<OrderResponse> orderResponses = orderMap.values().stream().map(order -> {
            PaymentDetails paymentDetails = paymentMap.get(order.getId());
            List<OrderItems> orderItems = orderItemMap.get(order.getId());
            return OrderResponse.builder()
                    .orderId(String.valueOf(order.getId()))
                    .total(order.getTotal())
                    .paymentAmount(paymentDetails.getAmount())
                    .paymentId(String.valueOf(paymentDetails.getId()))
                    .paymentStatusDesc(paymentDetails.getStatus().name())
                    .paymentStatus(paymentDetails.getStatus())
                    .paymentProvider(paymentDetails.getProvider().name())
                    .paymentProviderDesc(paymentDetails.getProvider().name())
                    .status(order.getStatus())
                    .items(mapItemToOerItemResponse(orderItems, bookMap))
                    .build();
        }).toList();

        log.info("End create order success");
        return orderResponses;
    }

    @NotNull
    private List<OrderItemDto> mapItemToOerItemResponse(List<OrderItems> orderItems, Map<Long, Book> bookMap) {
        return orderItems.stream().map(item -> {
            OrderItemDto orderItemDto = modelMapper.map(item, OrderItemDto.class);
            Book book = bookMap.get(item.getBookId());
            if (nonNull(book)) {
                orderItemDto.setTitle(book.getTitle());
                orderItemDto.setUrlThumbnail(book.getUrlThumbnail());
            }
            return orderItemDto;
        }).toList();
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        log.info("Start get order");
        OrderDetails order = orderDetailsRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.info("Order not found, throw exception");
            throw new BusinessException(ResponseCode.ORDER_NOT_FOUND);
        }
        PaymentDetails payment = paymentRepository.findByOrderId(orderId).orElse(null);
        if (payment == null) {
            log.info("Payment not found, throw exception");

        }
        log.info("Start create order response");
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(String.valueOf(order.getId()))
                .total(order.getTotal())
                .paymentAmount(nonNull(payment) ? payment.getAmount() : null)
                .paymentId(String.valueOf(nonNull(payment) ? payment.getId() : null))
                .paymentStatusDesc(nonNull(payment) ? payment.getStatus().name() : null)
                .paymentStatus(nonNull(payment) ? payment.getStatus() : null)
                .paymentProvider(nonNull(payment) ? payment.getProvider().name() : null)
                .paymentProviderDesc(nonNull(payment) ? payment.getProvider().name() : null)
                .build();

        List<OrderItems> orderItems = orderItemsRepository.findAllByOrderId(orderId);

        if (!CollectionUtils.isEmpty(orderItems)) {
            List<Book> books = bookRepository.findAllById(orderItems.stream().map(OrderItems::getBookId).toList());
            Map<Long, Book> bookMap = books.stream().collect(toMap(Book::getId, Function.identity()));
            List<OrderItemDto> itemDtos = mapItemToOerItemResponse(orderItems, bookMap);
            orderResponse.setItems(itemDtos);
        }

        log.info("End create order response");
        log.info("End get order success");
        return orderResponse;
    }

    @Override
    public void updatePaymentStatus(Long orderId, PaymentStatusRequest request) {
        log.info("Start update payment status");
        OrderDetails order = checkExistOrder(orderId);
        PaymentDetails payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        if (payment == null) {
            log.info("Payment not found, throw exception");
            throw new BusinessException(ResponseCode.PAYMENT_NOT_FOUND);
        }

        switch (request.getStatus()) {
            case PAID:
                if (payment.getProvider() != PaymentProvider.COD) {
                    order.setStatus(OrderStatus.PROCESSING);
                } else {
                    order.setStatus(OrderStatus.COMPLETED);
                }
                break;
            case CANCELLED:
                order.setStatus(OrderStatus.CANCELLED);
                break;
            default:
                break;
        }
        payment.setStatus(request.getStatus());
        paymentRepository.save(payment);
        orderDetailsRepository.save(order);
        //Todo save history or notification
        log.info("End update payment status");
    }

    @Override
    public void updateStatus(Long orderId, OrderStatusRequest request) {
        log.info("Start update order status");
        OrderDetails order = checkExistOrder(orderId);
        order.setStatus(request.getStatus());
        orderDetailsRepository.save(order);
        //Todo save history or notification
        log.info("End update order status");
    }

    @Override
    public PageDto<OrderResponse> getOrders(@Valid SearchOrderRequest request) {
        log.info("Start get orders");
        Account currentAccount = authService.getCurrentAccountLogin();
        if (isNull(currentAccount)) {
            log.info("Current account not found, throw exception");
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_FOUND);
        }
        Long id = currentAccount.getId();
        UserType userType = currentAccount.getType();
        List<String> sort = request.getSort();
        if (CollectionUtils.isEmpty(sort)) {
            sort = List.of("createdAt");
        }
        log.info("Searching books with request {}.", request);

        Sort sortBy = Sort.by(sort.stream().map(Sort.Order::by).toList());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize()).withSort(sortBy);

        Page<OrderDetails> orders = switch (userType) {
            case CUSTOMER -> orderDetailsRepository.findAllByUserId(id, pageable);
            case SELLER -> orderDetailsRepository.findAllBySellerId(id, pageable);
            default -> new PageImpl<>(List.of());
        };
        log.info("Got {} orders.", orders.getTotalElements());
        List<OrderResponse> orderResponses = orders.stream().map(order -> modelMapper.map(order, OrderResponse.class)).toList();
        log.info("End get orders success");
        return PageDto.<OrderResponse>builder().content(orderResponses)
                .totalElements(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .page(orders.getNumber())
                .size(orders.getSize())
                .build();
    }


    private List<PaymentDetails> createPayment(OrderRequest request, User savedUser, List<OrderDetails> savedOrder) {
        log.info("Start create payment");
        //create payment
        List<PaymentDetails> payments = savedOrder.stream().map(order -> {
            return PaymentDetails.builder()
                    .userId(isNull(savedUser) ? null : savedUser.getId())
                    .orderId(order.getId())
                    .provider(request.getPaymentProvider())
                    .amount(order.getTotal())
                    .status(PENDING)
                    .build();
        }).toList();
        List<PaymentDetails> savedPayments = paymentRepository.saveAll(payments);
        log.info("End create payment");
        return savedPayments;
    }

    private List<OrderDetails> saveOrderDetail(User savedUser, ShoppingCart cart, List<CartItem> cartItems, LocalDateTime now) {
        log.info("Start create order detail");
        //collect item to map sellerId and total
        Map<Long, BigDecimal> sellerTotalMap =
                cartItems.stream().collect(toMap(CartItem::getSellerId, CartItem::getTotal, BigDecimal::add));
        //create order detail for each seller
        List<OrderDetails> orderDetails = sellerTotalMap.entrySet().stream().map(entry -> OrderDetails.builder()
                .userId(isNull(savedUser) ? null : savedUser.getId())
                .total(entry.getValue())
                .status(OrderStatus.PENDING)
                .sellerId(entry.getKey())
                .createdAt(now)
                .updatedAt(now)
                .createdBy(savedUser.getName())
                .updatedBy(savedUser.getName())
                .build()).toList();
        //save order detail
        List<OrderDetails> savedOrders = orderDetailsRepository.saveAll(orderDetails);

        log.info("End create order");
        return savedOrders;
    }

    private void updateBooksQuantity(List<CartItem> cartItems) {
        log.info("Start update book quantity");
        List<Long> bookIds = cartItems.stream().map(CartItem::getBookId).toList();
        List<Book> books = bookRepository.findAllById(bookIds);
        for (Book book : books) {
            for (CartItem item : cartItems) {
                if (book.getId().equals(item.getBookId())) {
                    book.setQuantity(book.getQuantity() - item.getQuantity());
                }
            }
        }
        bookRepository.saveAll(books);
        log.info("End update book quantity");
    }

    private List<OrderItems> createOrderItems(List<CartItem> cartItems, List<OrderDetails> savedOrders) {
        log.info("Start create order item");
        //collect item to map bookId and order detail id
        Map<Long, Long> bookOrderMap =
                savedOrders.stream().collect(toMap(OrderDetails::getSellerId, OrderDetails::getId));
        //collect item to map sellerId and lis OrderItems
        Map<Long, List<CartItem>> sellerItemMap =
                cartItems.stream().collect(groupingBy(CartItem::getSellerId));
        //create order item for each seller
        List<OrderItems> orderItems = sellerItemMap.entrySet().stream().flatMap(entry -> {
            Long orderId = bookOrderMap.get(entry.getKey());
            return entry.getValue().stream().map(item -> {
                OrderItems orderItem = modelMapper.map(item, OrderItems.class);
                orderItem.setOrderId(orderId);
                return orderItem;
            });
        }).toList();

        List<OrderItems> saved = orderItemsRepository.saveAll(orderItems);

        log.info("End create order item");
        return saved;
    }
}
