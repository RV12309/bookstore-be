package com.hust.bookstore.enumration;

import lombok.Getter;

@Getter
public enum PaymentProvider {
    PAYPAL("PAYPAL", "Paypal"),
    STRIPE("STRIPE", "Stripe"),
    BANKING("BANKING", "Chuyển khoản"),
    COD("COD", "Thanh toán khi nhận hàng");

    private String code;
    private String description;

    PaymentProvider(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
