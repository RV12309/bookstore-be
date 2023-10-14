package com.hust.bookstore.enumration;

public enum ResponseCode {
    SUCCESS("00", "Thành công"),
    FAIL("96", "Thất bại"),
    USER_NOT_FOUND("USR-001", "Không tìm thấy người dùng"),
    USER_EXISTED("USR-002", "Người dùng đã tồn tại"),
    EMAIL_EXISTED("USR-003", "Email đã tồn tại"),
    PHONE_EXISTED("USR-004", "Số điện thoại đã tồn tại"),
    ACCOUNT_NOT_FOUND("ACC-001", "Không tìm thấy tài khoản"),
    ACCOUNT_ALREADY_ACTIVE("ACC-002", "Tài khoản đã được kích hoạt"),
    VERIFICATION_CODE_EXPIRED("ACC-003", "Mã xác thực đã hết hạn"),
    VERIFICATION_CODE_FAIL("ACC-004", "Mã xác thực không đúng"),
    ;

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
