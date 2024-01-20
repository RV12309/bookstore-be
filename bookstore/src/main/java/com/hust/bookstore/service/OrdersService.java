package com.hust.bookstore.service;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.response.OrderResponse;
import com.hust.bookstore.dto.response.OrderStatisticResponse;
import com.hust.bookstore.dto.response.RevenueStatisticResponse;
import com.hust.bookstore.enumration.OrderStatisticType;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersService {
    List<OrderResponse> createOrder(OrderRequest request);

    OrderResponse getOrder(Long orderId);

    void updatePaymentStatus(Long orderId, PaymentStatusRequest request);

    void updateStatus(Long orderId, OrderStatusRequest request);

    PageDto<OrderResponse> getOrders(@Valid SearchOrderRequest request);

    void cancelOrder(Long orderId, OrderCancelRequest request);

    List<OrderStatisticResponse> statisticOrder(LocalDateTime from, LocalDateTime to, OrderStatisticType type);

    List<RevenueStatisticResponse> statisticRevenue(LocalDateTime from, LocalDateTime to, OrderStatisticType type);

    OrderCallbackRequest callback(OrderCallbackRequest request);
}
