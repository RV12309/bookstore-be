package com.hust.bookstore.service;

import com.hust.bookstore.dto.request.OrderRequest;
import com.hust.bookstore.dto.request.OrderStatusRequest;
import com.hust.bookstore.dto.request.PaymentStatusRequest;
import com.hust.bookstore.dto.response.OrderResponse;

public interface OrdersService {
    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrder(Long orderId);

    void updatePaymentStatus(Long orderId, PaymentStatusRequest request);

    void updateStatus(Long orderId, OrderStatusRequest request);
}
