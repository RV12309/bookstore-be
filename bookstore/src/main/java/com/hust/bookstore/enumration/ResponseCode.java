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
    ACCOUNT_NOT_VERIFY("ACC-005", "Tài khoản chưa được kích hoạt"),
    ACCOUNT_LOCKED("ACC-006", "Tài khoản đã bị khóa"),

    // Book
    BOOK_NOT_FOUND("BOK-001", "Không tìm thấy sách"),
    BOOK_EXISTED("BOK-002", "Sách đã tồn tại"),
    BOOK_NOT_AVAILABLE("BOK-003", "Sách không còn sẵn có"),
    BOOK_IS_DELETED("BOK-004", "Sách đã bị xóa"),

    // Category
    CATEGORY_NOT_FOUND("CAT-001", "Không tìm thấy danh mục"),
    CATEGORY_ALREADY_EXIST("CAT-002", "Danh mục đã tồn tại"),
    CATEGORY_IS_IN_USE("CAT-003", "Danh mục đang được sử dụng"),
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
