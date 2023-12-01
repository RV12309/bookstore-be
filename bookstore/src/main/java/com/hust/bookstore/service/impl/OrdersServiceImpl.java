package com.hust.bookstore.service.impl;

import com.hust.bookstore.dto.OrderItemDto;
import com.hust.bookstore.dto.request.OrderRequest;
import com.hust.bookstore.dto.request.OrderStatusRequest;
import com.hust.bookstore.dto.request.PaymentStatusRequest;
import com.hust.bookstore.dto.response.OrderResponse;
import com.hust.bookstore.entity.*;
import com.hust.bookstore.enumration.Gender;
import com.hust.bookstore.enumration.OrderStatus;
import com.hust.bookstore.enumration.PaymentProvider;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.NotificationService;
import com.hust.bookstore.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.hust.bookstore.enumration.PaymentStatus.PENDING;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
    public OrderResponse createOrder(OrderRequest request) {
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

        OrderDetails savedOrder = saveOrderDetail(savedUser, cart);
        createOrderItems(cartItems, savedOrder);
        PaymentDetails payment = createPayment(request, savedUser, savedOrder);

        updateBooksQuantity(cartItems);

        log.info("Start delete cart item");
        cartItemRepository.deleteAll(cartItems);
        log.info("End delete cart item");

        log.info("Start delete cart");
        cartRepository.delete(cart);
        log.info("End delete cart");

        log.info("Start create order response");
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(String.valueOf(savedOrder.getId()))
                .total(savedOrder.getTotal())
                .paymentAmount(payment.getAmount())
                .paymentId(String.valueOf(payment.getId()))
                .paymentStatusDesc(payment.getStatus().name())
                .paymentStatus(payment.getStatus())
                .paymentProvider(payment.getProvider().name())
                .paymentProviderDesc(payment.getProvider().name())
                .build();

        log.info("End create order response");
        log.info("End create order success");
        return orderResponse;
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
            List<OrderItemDto> itemDtos = orderItems.stream().map(item -> {
                OrderItemDto orderItemDto = modelMapper.map(item, OrderItemDto.class);
                Book book = bookMap.get(item.getBookId());
                if (nonNull(book)) {
                    orderItemDto.setTitle(book.getTitle());
                    orderItemDto.setUrlThumbnail(book.getUrlThumbnail());
                }
                return orderItemDto;
            }).toList();
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


    private PaymentDetails createPayment(OrderRequest request, User savedUser, OrderDetails savedOrder) {
        log.info("Start create payment");
        PaymentDetails payment = PaymentDetails.builder()
                .userId(isNull(savedUser) ? null : savedUser.getId())
                .orderId(savedOrder.getId())
                .provider(request.getPaymentProvider())
                .amount(savedOrder.getTotal())
                .status(PENDING)
                .build();
        paymentRepository.save(payment);
        log.info("End create payment");
        return payment;
    }

    private OrderDetails saveOrderDetail(User savedUser, ShoppingCart cart) {
        OrderDetails order = OrderDetails.builder()
                .userId(isNull(savedUser) ? null : savedUser.getId())
                .total(cart.getTotal())
                .status(OrderStatus.PENDING)
                .build();
        OrderDetails savedOrder = orderDetailsRepository.save(order);
        log.info("End create order");
        return savedOrder;
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

    private void createOrderItems(List<CartItem> cartItems, OrderDetails savedOrder) {
        log.info("Start create order item");
        List<OrderItems> orderItems = cartItems.stream().map(item -> {
            OrderItems orderItem = modelMapper.map(item, OrderItems.class);
            orderItem.setOrderId(savedOrder.getId());
            return orderItem;
        }).toList();
        orderItemsRepository.saveAll(orderItems);

        log.info("End create order item");
    }
}
