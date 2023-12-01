package com.hust.bookstore.enumration;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("PENDING", "Chờ thanh toán"),
    PAID("PAID", "Đã thanh toán"),
    CANCELLED("CANCELLED", "Đã hủy"),
    REFUNDED("REFUNDED", "Đã hoàn tiền");

    private final String code;
    private final String description;

    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
