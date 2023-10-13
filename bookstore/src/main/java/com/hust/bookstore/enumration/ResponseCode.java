package com.hust.bookstore.enumration;

public enum ResponseCode {
    USER_NOT_FOUND("USR-001", "Không tìm thấy người dùng"),
    USER_EXISTED("USR-002", "Người dùng đã tồn tại"),
    EMAIL_EXISTED("USR-003", "Email đã tồn tại"),
    PHONE_EXISTED("USR-004", "Số điện thoại đã tồn tại");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
