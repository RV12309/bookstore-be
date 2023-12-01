package com.hust.bookstore.enumration;

import lombok.Getter;

@Getter
public enum CartAction {
    ADD("ADD", "Thêm vào giỏ hàng"),
    UPDATE("UPDATE", "Cập nhật giỏ hàng"),
    REMOVE("REMOVE", "Xóa khỏi giỏ hàng");

    private final String code;
    private final String description;

    CartAction(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
