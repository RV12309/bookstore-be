package com.hust.bookstore.dto.response;

import com.hust.bookstore.dto.OrderItemDto;
import com.hust.bookstore.enumration.OrderStatus;
import com.hust.bookstore.enumration.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderResponse {
    String orderId;
    BigDecimal total;
    String paymentId;
    String paymentProvider;
    String paymentProviderDesc;
    PaymentStatus paymentStatus;
    String paymentStatusDesc;
    BigDecimal paymentAmount;
    OrderStatus status;
    List<OrderItemDto> items;

}
