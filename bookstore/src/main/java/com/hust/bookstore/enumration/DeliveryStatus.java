package com.hust.bookstore.enumration;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    PENDING("PENDING", "Đang chờ"),
    SHIPPING("SHIPPING", "Đang giao"),
    DELIVERED("DELIVERED", "Đã giao"),
    CANCELLED("CANCELLED", "Đã hủy")

    ;

    private String code;
    private String description;
    DeliveryStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
