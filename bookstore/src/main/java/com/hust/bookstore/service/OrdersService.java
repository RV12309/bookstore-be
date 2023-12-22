package com.hust.bookstore.service;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.OrderRequest;
import com.hust.bookstore.dto.request.OrderStatusRequest;
import com.hust.bookstore.dto.request.PaymentStatusRequest;
import com.hust.bookstore.dto.request.SearchOrderRequest;
import com.hust.bookstore.dto.response.OrderResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface OrdersService {
    List<OrderResponse> createOrder(OrderRequest request);

    OrderResponse getOrder(Long orderId);

    void updatePaymentStatus(Long orderId, PaymentStatusRequest request);

    void updateStatus(Long orderId, OrderStatusRequest request);

    PageDto<OrderResponse> getOrders(@Valid SearchOrderRequest request);
}
