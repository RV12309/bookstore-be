package com.hust.bookstore.dto.request;

import com.hust.bookstore.enumration.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentStatusRequest {

    @NotNull
    private PaymentStatus status;
}
