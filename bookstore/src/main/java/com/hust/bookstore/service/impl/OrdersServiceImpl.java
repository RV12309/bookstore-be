package com.hust.bookstore.service.impl;

import com.hust.bookstore.dto.OrderItemDto;
import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.request.delivery.DeliveryRequest;
import com.hust.bookstore.dto.response.OrderResponse;
import com.hust.bookstore.dto.response.OrderStatisticResponse;
import com.hust.bookstore.dto.response.RevenueStatisticResponse;
import com.hust.bookstore.entity.*;
import com.hust.bookstore.enumration.*;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.repository.projection.StatOderProjection;
import com.hust.bookstore.repository.projection.StatOderTypeProjection;
import com.hust.bookstore.repository.projection.StatRevenueProjection;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.DeliveryPartnerService;
import com.hust.bookstore.service.NotificationService;
import com.hust.bookstore.service.OrdersService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.hust.bookstore.common.Constants.*;
import static com.hust.bookstore.common.Utils.dateTimeFormatter;
import static com.hust.bookstore.common.Utils.formatter;
import static com.hust.bookstore.enumration.OrderStatus.COMPLETED;
import static com.hust.bookstore.enumration.PaymentStatus.PENDING;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
public class OrdersServiceImpl extends BusinessHelper implements OrdersService {
    @Value("${endpoint.partner.ghn.order.tracking}")
    private String trackingUrl;
    private final DeliveryPartnerService deliveryPartnerService;

    public OrdersServiceImpl(BookRepository bookRepository, CartRepository cartRepository,
                             CartItemRepository cartItemRepository, PaymentRepository paymentRepository,
                             DeliveryPartnerConfigRepository deliveryPartnerConfigRepo,
                             StoreDeliveryPartnerRepository storeDeliveryPartnerRepo, UserRepository userRepository,
                             OrderDetailRepository orderDetailsRepository, OrderItemsRepository orderItemsRepository,
                             CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository,
                             AccountRepository accountRepository, AuthService authService,
                             BookImageRepository bookImageRepository, ModelMapper modelMapper,
                             NotificationService notificationService, UserAddressRepository addressRepository,
                             DeliveryPartnerService deliveryPartnerService, DeliveryDetailRepository deliveryDetailRepository) {
        super(bookRepository, cartRepository, cartItemRepository, paymentRepository,
                deliveryPartnerConfigRepo, storeDeliveryPartnerRepo, userRepository,
                orderDetailsRepository, orderItemsRepository, categoryRepository,
                bookCategoryRepository, accountRepository, authService, bookImageRepository,
                modelMapper, notificationService, addressRepository, deliveryDetailRepository);
        this.deliveryPartnerService = deliveryPartnerService;
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
                        .type(UserType.CUSTOMER)
                        .accountId(currentAccount.getId())
                        .firstAddress(request.getFirstAddress())
                        .ward(request.getWard())
                        .district(request.getDistrict())
                        .province(request.getProvince())
                        .wardCode(request.getWardCode())
                        .districtId(request.getDistrictId())
                        .provinceId(request.getProvinceId())
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
                    .type(UserType.GUEST)
                    .firstAddress(request.getFirstAddress())
                    .ward(request.getWard())
                    .district(request.getDistrict())
                    .province(request.getProvince())
                    .wardCode(request.getWardCode())
                    .districtId(request.getDistrictId())
                    .provinceId(request.getProvinceId())
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
        if (nonNull(cart.getUserId())) {
            log.info("Start delete cart");
            cartRepository.delete(cart);
            log.info("End delete cart");
        }

        List<DeliveryDetails> deliveryDetails = new ArrayList<>();
        for (OrderDetails order : savedOrders) {
            DeliveryDetails deliveryDetail = DeliveryDetails.builder()
                    .orderId(order.getId())
                    .status(DeliveryStatus.PENDING)
                    .firstAddress(request.getFirstAddress())
                    .ward(request.getWard())
                    .district(request.getDistrict())
                    .province(request.getProvince())
                    .wardCode(request.getWardCode())
                    .districtId(request.getDistrictId())
                    .provinceId(request.getProvinceId())
                    .build();
            deliveryDetails.add(deliveryDetail);
        }
        deliveryDetailRepository.saveAll(deliveryDetails);
        log.info("Save delivery details success");

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
        List<OrderItemDto> items = new ArrayList<>();
        List<OrderResponse> orderResponses = orderMap.values().stream().map(order -> {
            PaymentDetails paymentDetails = paymentMap.get(order.getId());
            List<OrderItems> orderItems = orderItemMap.get(order.getId());
            List<OrderItemDto> itemDtos = mapItemToOerItemResponse(orderItems, bookMap);
            items.addAll(itemDtos);
            sendOrderNotification(request, savedUser, itemDtos, order, now);
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
                    .items(itemDtos)
                    .build();
        }).toList();
        log.info("End create order success");
        return orderResponses;
    }

    private void sendOrderNotification(OrderRequest request, User savedUser, List<OrderItemDto> items, OrderDetails order, LocalDateTime now) {
        log.info("Send notification email to {}", savedUser.getEmail());
        Context context = new Context();
        context.setVariable(ITEMS, items);
        context.setVariable(ORDER_ID, order.getId());
        context.setVariable(USERNAME, savedUser.getName());
        context.setVariable(EXPECTED_DATE, formatter.format(now.plusDays(3)));
        context.setVariable(TOLTAL, order.getTotal());
        context.setVariable(DETAIL_ADDRESS, request.getFirstAddress() + ", " + request.getWard() +
                ", " + request.getDistrict() + ", " + request.getProvince());
        context.setVariable(PAYMENT_METHOD, request.getPaymentProvider().name());
        notificationService.send(MailTemplate.NEW_ORDER, context, savedUser.getEmail());
        log.info("End send notification email to {}", savedUser.getEmail());
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
        User buyer = userRepository.findById(order.getUserId()).orElse(null);
        if (buyer == null) {
            log.info("Buyer not found, throw exception");
        }
        Account account = accountRepository.findById(order.getSellerId()).orElse(null);
        User seller = null;
        if (nonNull(account)) {
            seller = userRepository.findById(account.getUserId()).orElse(null);
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
                .status(order.getStatus())
                .fromAddress(isNull(seller) ? null : seller.getFirstAddress() + ", " + seller.getWard() +
                        ", " + seller.getDistrict() + ", " + seller.getProvince())
                .toAddress(isNull(buyer) ? null : buyer.getFirstAddress() + ", " + buyer.getWard() + ", "
                        + buyer.getDistrict() + ", " + buyer.getProvince())
                .senderName(isNull(seller) ? null : seller.getName())
                .buyerName(isNull(buyer) ? null : buyer.getName())
                .build();

        List<OrderItems> orderItems = orderItemsRepository.findAllByOrderId(orderId);

        if (!CollectionUtils.isEmpty(orderItems)) {
            List<Book> books = bookRepository.findAllById(orderItems.stream().map(OrderItems::getBookId).toList());
            Map<Long, Book> bookMap = books.stream().collect(toMap(Book::getId, Function.identity()));
            List<OrderItemDto> itemDtos = mapItemToOerItemResponse(orderItems, bookMap);
            orderResponse.setItems(itemDtos);
        }
        DeliveryDetails deliveryDetails = deliveryDetailRepository.findByOrderId(orderId).orElse(null);
        if (nonNull(deliveryDetails)) {
            orderResponse.setTrackingCode(deliveryDetails.getTrackingCode());
            String urlTracking = trackingUrl + deliveryDetails.getTrackingCode();
            orderResponse.setUrlTracking(urlTracking);
        }

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
                    order.setStatus(COMPLETED);
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
    @Transactional
    public void updateStatus(Long orderId, OrderStatusRequest request) {
        log.info("Start update order status");
        OrderDetails order = checkExistOrder(orderId);
        order.setStatus(request.getStatus());
        orderDetailsRepository.save(order);
        if (OrderStatus.PROCESSING.equals(request.getStatus())) {
            createShipping(order);
        }
        if (OrderStatus.CANCELLED.equals(request.getStatus())) {
            sendCancelOrderToDelivery(order);
        }
        if (COMPLETED.equals(request.getStatus())) {
            PaymentDetails payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
            if (payment == null) {
                log.info("Payment not found");
                return;
            }
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            log.info("Update payment status success");
        }
        //Todo save history or notification
        log.info("End update order status");
    }

    private void sendCancelOrderToDelivery(OrderDetails order) {
        log.info("Start send cancel order to delivery");
        DeliveryDetails deliveryDetails = deliveryDetailRepository.findByOrderId(order.getId()).orElse(null);
        if (isNull(deliveryDetails)) {
            log.info("Delivery details not found, throw exception");
            return;
        }
        deliveryPartnerService.cancelOrder(deliveryDetails.getTrackingCode());
        deliveryDetails.setStatus(DeliveryStatus.CANCELLED);
        deliveryDetailRepository.save(deliveryDetails);
        log.info("End send cancel order to delivery");
    }

    private void createShipping(OrderDetails order) {
        log.info("Start create shipping");
        Account seller = authService.getCurrentAccountLogin();
        if (isNull(seller)) {
            log.info("Current account not found, throw exception");
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_FOUND);
        }
        User sellerUser = userRepository.findById(seller.getUserId()).orElse(null);

        List<OrderItems> orderItems = orderItemsRepository.findAllByOrderId(order.getId());
        if (CollectionUtils.isEmpty(orderItems)) {
            log.info("Order items not found, throw exception");
            throw new BusinessException(ResponseCode.ORDER_ITEM_NOT_FOUND);
        }
        List<Long> bookIds = orderItems.stream().map(OrderItems::getBookId).toList();
        List<Book> books = bookRepository.findAllById(bookIds);
        Map<Long, Book> bookMap = books.stream().collect(toMap(Book::getId, Function.identity()));
        User user = userRepository.findById(order.getUserId()).orElse(null);
        if (isNull(user)) {
            log.info("Account not found, throw exception");
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_FOUND);
        }
        PaymentDetails payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        DeliveryDetails deliveryDetails = deliveryDetailRepository.findByOrderId(order.getId()).orElse(null);
        DeliveryRequest deliveryRequest = DeliveryRequest.builder()
                .paymentTypeId(isNull(payment) ? 2 : (payment.getProvider() == PaymentProvider.COD ? 2 : 1))
                .note("Something")
                .requiredNote("KHONGCHOXEMHANG")
                .fromName(isNull(sellerUser) ? "Bookstore" : sellerUser.getName())
                .fromPhone(isNull(sellerUser) ? null : sellerUser.getPhone())
                .fromAddress(isNull(sellerUser) ? null : sellerUser.getFirstAddress() +
                        ", " + sellerUser.getWard() + ", " + sellerUser.getDistrict() + ", " + sellerUser.getProvince())
                .fromWardName(isNull(sellerUser) ? "" : sellerUser.getWard())
                .fromDistrictName(sellerUser.getDistrict())
                .fromProvinceName(sellerUser.getProvince())
                .fromWardCode(String.valueOf(sellerUser.getWardCode()))
                .returnPhone(sellerUser.getPhone())
                .returnDistrictId(sellerUser.getDistrictId())
                .returnWardCode(String.valueOf(sellerUser.getWardCode()))
                .clientOrderCode(String.valueOf(order.getId()))
                .toName(user.getName())
                .toPhone(user.getPhone())
                .toAddress(isNull(deliveryDetails) ? "" : (deliveryDetails.getFirstAddress() + ", " + deliveryDetails.getWard() + ", "
                        + deliveryDetails.getDistrict() + ", " + deliveryDetails.getProvince()))
                .toWardCode(isNull(deliveryDetails) ? "" : String.valueOf(deliveryDetails.getWardCode()))
                .toDistrictId(isNull(deliveryDetails) ? 0 : deliveryDetails.getDistrictId())
                .codAmount(order.getTotal().intValue())
                .content("Something")
                .weight(200)
                .length(1)
                .width(19)
                .height(10)
                .serviceTypeId(2)
                .serviceId(53320)//hàng nhẹ
                .coupon("")
                .pickShift(List.of(2, 3, 4))
                .items(orderItems.stream().map(item -> {
                    Book book = bookMap.get(item.getBookId());
                    return DeliveryRequest.Item.builder()
                            .code(String.valueOf(book.getId()))
                            .name(book.getTitle())
                            .quantity(Math.toIntExact(item.getQuantity()))
                            .price(book.getPrice().intValue())
                            .length(1)
                            .width(19)
                            .height(10)
                            .weight(200)
                            .category(DeliveryRequest.Category.builder()
                                    .level1(book.getTitle())
                                    .build())
                            .build();
                }).toList())
                .build();

        String trackingCode = deliveryPartnerService.createOrder(deliveryRequest);
        if (nonNull(deliveryDetails)) {
            deliveryDetails.setTrackingCode(trackingCode);
            deliveryDetailRepository.save(deliveryDetails);
            log.info("Update tracking code success");
        }

        log.info("End create shipping");
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
            case CUSTOMER -> orderDetailsRepository.findAllByUserId(currentAccount.getUserId(), pageable);
            case SELLER -> orderDetailsRepository.findAllBySellerId(id, pageable);
            default -> new PageImpl<>(List.of());
        };
        log.info("Got {} orders.", orders.getTotalElements());
        List<PaymentDetails> payments = paymentRepository.findAllByOrderIdIn(orders.getContent().stream().map(OrderDetails::getId).toList());
        Map<Long, PaymentDetails> paymentMap = payments.stream().collect(toMap(PaymentDetails::getOrderId, Function.identity()));
        List<OrderResponse> orderResponses = orders.getContent().stream().map(order -> {
            PaymentDetails payment = paymentMap.get(order.getId());
            return OrderResponse.builder()
                    .orderId(String.valueOf(order.getId()))
                    .total(order.getTotal())
                    .paymentAmount(nonNull(payment) ? payment.getAmount() : null)
                    .paymentId(String.valueOf(nonNull(payment) ? payment.getId() : null))
                    .paymentStatusDesc(nonNull(payment) ? payment.getStatus().name() : null)
                    .paymentStatus(nonNull(payment) ? payment.getStatus() : null)
                    .paymentProvider(nonNull(payment) ? payment.getProvider().name() : null)
                    .paymentProviderDesc(nonNull(payment) ? payment.getProvider().name() : null)
                    .status(order.getStatus())
                    .userId(String.valueOf(order.getUserId()))
                    .sellerId(String.valueOf(order.getSellerId()))
                    .createdAt(dateTimeFormatter.format(order.getCreatedAt()))
                    .updatedAt(dateTimeFormatter.format(order.getUpdatedAt()))
                    .createdBy(order.getCreatedBy())
                    .build();
        }).toList();
        log.info("End get orders success");
        return PageDto.<OrderResponse>builder()
                .content(orderResponses)
                .totalElements(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .page(orders.getNumber())
                .size(orders.getSize())
                .build();
    }

    @Override
    public void cancelOrder(Long orderId, OrderCancelRequest request) {
        log.info("Start cancel order with id {}", orderId);
        OrderDetails order = checkExistOrder(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            log.info("Order status is not pending, throw exception");
            throw new BusinessException(ResponseCode.ORDER_STATUS_NOT_PENDING);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderDetailsRepository.save(order);
        //Todo save history or notification
        log.info("End cancel order success");

    }

    @Override
    public List<OrderStatisticResponse> statisticOrder(LocalDateTime from, LocalDateTime to, OrderStatisticType type) {
        log.info("Start statistic order by type {}", type);
        List<OrderStatisticResponse> responses = new ArrayList<>();
        switch (type) {
            case MONTH -> {
                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusMonths(1);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatOderProjection> months = orderDetailsRepository.statisticOrderMonth(from, to);

                for (StatOderProjection month : months) {
                    responses.add(OrderStatisticResponse.builder()
                            .time(month.getTime())
                            .totalOrder(month.getTotalOrder())
                            .totalAmount(month.getTotalAmount())
                            .build());
                }
            }
            case QUARTER -> {
                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusMonths(3);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatOderProjection> days = orderDetailsRepository.statisticOrderQuater(from, to);
                //Map local date time to total order , key is local date time with start week
                for (StatOderProjection month : days) {
                    responses.add(OrderStatisticResponse.builder()
                            .time(month.getTime())
                            .totalOrder(month.getTotalOrder())
                            .totalAmount(month.getTotalAmount())
                            .build());
                }

            }
            case YEAR -> {
                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusYears(1);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatOderProjection> years = orderDetailsRepository.statisticOrderYear(from, to);
                for (StatOderProjection month : years) {
                    responses.add(OrderStatisticResponse.builder()
                            .time(month.getTime())
                            .totalOrder(month.getTotalOrder())
                            .totalAmount(month.getTotalAmount())
                            .build());
                }
            }
            case WEEK -> {

                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusWeeks(1);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatOderProjection> weeks = orderDetailsRepository.statisticOrderWeek(from, to);
                //map for each day of week , if not exist set total order is 0
                for (StatOderProjection month : weeks) {
                    responses.add(OrderStatisticResponse.builder()
                            .time(month.getTime())
                            .totalOrder(month.getTotalOrder())
                            .totalAmount(month.getTotalAmount())
                            .build());
                }

            }
            default -> {
                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusDays(1);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatOderProjection> days = orderDetailsRepository.statisticOrderDay(from, to);

                for (StatOderProjection month : days) {
                    responses.add(OrderStatisticResponse.builder()
                            .time(month.getTime())
                            .totalOrder(month.getTotalOrder())
                            .totalAmount(month.getTotalAmount())
                            .build());
                }
            }
        }
        log.info("End statistic order by type {}", type);
        return responses;
    }

    @Override
    public List<RevenueStatisticResponse> statisticRevenue(LocalDateTime from, LocalDateTime to, OrderStatisticType type) {
        log.info("Start statistic order by type {}", type);
        List<RevenueStatisticResponse> responses = new ArrayList<>();
        switch (type) {
            case MONTH -> {
                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusMonths(1);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatRevenueProjection> months = orderDetailsRepository.statisticRevenueMonth(from, to);
                for (StatRevenueProjection month : months) {
                    responses.add(RevenueStatisticResponse.builder()
                            .time(month.getTime())
                            .totalAmount(month.getTotalAmount())
                            .build());
                }
            }
            case QUARTER -> {
                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusMonths(3);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatRevenueProjection> days = orderDetailsRepository.statisticRevenueQuater(from, to);
                for (StatRevenueProjection month : days) {
                    responses.add(RevenueStatisticResponse.builder()
                            .time(month.getTime())
                            .totalAmount(month.getTotalAmount())
                            .build());
                }
            }
            case YEAR -> {
                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusYears(1);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatRevenueProjection> years = orderDetailsRepository.statisticRevenueYear(from, to);
                for (StatRevenueProjection year : years) {
                    responses.add(RevenueStatisticResponse.builder()
                            .time(year.getTime())
                            .totalAmount(year.getTotalAmount())
                            .build());
                }
            }
            case WEEK -> {

                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusWeeks(1);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatRevenueProjection> weeks = orderDetailsRepository.statisticRevenueWeek(from, to);
                for (StatRevenueProjection week : weeks) {
                    responses.add(RevenueStatisticResponse.builder()
                            .time(week.getTime())
                            .totalAmount(week.getTotalAmount())
                            .build());
                }
            }
            default -> {
                LocalDateTime now = LocalDateTime.now();
                if (isNull(from)) {
                    from = now.minusDays(1);
                }
                if (isNull(to)) {
                    to = now;
                }

                List<StatRevenueProjection> days = orderDetailsRepository.statisticRevenueOrderDay(from, to);
                for (StatRevenueProjection month : days) {
                    responses.add(RevenueStatisticResponse.builder()
                            .time(month.getTime())
                            .totalAmount(month.getTotalAmount())
                            .build());
                }
            }
        }
        log.info("End statistic order by type {}", type);
        return responses;
    }

    @Override
    public OrderCallbackRequest callback(OrderCallbackRequest request) {
        log.info("Start callback order");

        log.info("Request {}", request);
        return request;
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
