package com.hust.bookstore.dto.request;

import com.hust.bookstore.enumration.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderStatusRequest {

    @NotNull
    private OrderStatus status;

    private String note;
}
