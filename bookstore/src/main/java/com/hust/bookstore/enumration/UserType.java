package com.hust.bookstore.enumration;

import lombok.Getter;

@Getter
public enum UserType {
    ADMIN("ADMIN","Admin"),
    CUSTOMER("CUSTOMER","Khách hàng"),
    SELLER("SELLER","Người bán"),
    GUEST("GUEST","Khách vãng lai");

    private final String code;
    private final String name;

    UserType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
