package com.hust.bookstore.enumration;

public enum ResponseCode {
    SUCCESS("00", "Thành công"),
    FAIL("96", "Thất bại"),
    BAD_REQUEST("97", "Yêu cầu không hợp lệ"),
    UNAUTHORIZED("98", "Chưa xác thực"),
    USER_NOT_FOUND("USR-001", "Không tìm thấy người dùng"),
    USER_EXISTED("USR-002", "Người dùng đã tồn tại"),
    EMAIL_EXISTED("USR-003", "Email đã tồn tại"),
    PHONE_EXISTED("USR-004", "Số điện thoại đã tồn tại"),
    USER_NOT_MATCH("USR-005", "Người dùng không khớp với tài khoản hiện tại"),
    ACCOUNT_NOT_FOUND("ACC-001", "Không tìm thấy tài khoản"),
    ACCOUNT_ALREADY_ACTIVE("ACC-002", "Tài khoản đã được kích hoạt"),
    VERIFICATION_CODE_EXPIRED("ACC-003", "Mã xác thực đã hết hạn"),
    VERIFICATION_CODE_FAIL("ACC-004", "Mã xác thực không đúng"),
    ACCOUNT_NOT_VERIFY("ACC-005", "Tài khoản chưa được kích hoạt"),
    ACCOUNT_LOCKED("ACC-006", "Tài khoản đã bị khóa"),
    INVALID_USERNAME_OR_PASSWORD("ACC-007", "Tên đăng nhập hoặc mật khẩu không đúng"),

    // Book
    BOOK_NOT_FOUND("BOK-001", "Không tìm thấy sách"),
    BOOK_EXISTED("BOK-002", "Sách đã tồn tại"),
    BOOK_NOT_AVAILABLE("BOK-003", "Sách không còn sẵn có"),
    BOOK_IS_DELETED("BOK-004", "Sách đã bị xóa"),
    BOOK_OUT_OF_STOCK("BOK-005", "Sách đã hết hàng"),
    BOOK_NOT_ENOUGH("BOK-006", "Số lượng sách không đủ"),

    // Category
    CATEGORY_NOT_FOUND("CAT-001", "Không tìm thấy danh mục"),
    CATEGORY_ALREADY_EXIST("CAT-002", "Danh mục đã tồn tại"),
    CATEGORY_IS_IN_USE("CAT-003", "Danh mục đang được sử dụng"),

    //Shopping Cart
    CART_NOT_FOUND("CART-001", "Không tìm thấy giỏ hàng của bạn"),
    CART_ITEM_NOT_FOUND("CART-002", "Không tìm thấy sản phẩm trong giỏ hàng của bạn"),

    // Delivery Partner
    DELIVERY_PARTNER_NOT_FOUND("DEL-001", "Không tìm thấy đối tác vận chuyển"),
    CONFIG_DELIVERY_PARTNER_NOT_FOUND("DEL-002", "Không tìm thấy cấu hình đối tác vận chuyển"),
    CREATE_STORE_FAILED("DEL-003", "Tạo đối tác vận chuyển thất bại"),
    STORE_ALREADY_EXIST("DEL-004", "Đối tác vận chuyển đã tồn tại"),
    GET_PROVINCE_FAILED("DEL-005", "Lấy thông tin tỉnh/thành phố thất bại"),
    GET_DISTRICT_FAILED("DEL-006", "Lấy thông tin quận/huyện thất bại"),
    GET_WARD_FAILED("DEL-007", "Lấy thông tin phường/xã thất bại"),
    USER_ADDRESS_NOT_FOUND("ADD-001", "Không tìm thấy địa chỉ người dùng"),

    USER_ADDRESS_NOT_MATCH("ADD-002", "Địa chỉ người dùng không khớp"),
    USER_ADDRESS_DEFAULT("ADD-003", "Địa chỉ người dùng đã được đặt mặc định"),
    ORDER_NOT_FOUND("ORD-001", "Không tìm thấy đơn hàng"),
    PAYMENT_NOT_FOUND("PAY-001", "Không tìm thấy thông tin thanh toán"),
    GET_SHIPPING_SERVICE_FAILED("SHI-001", "Lấy thông tin dịch vụ vận chuyển thất bại"),
    INVALID_REFRESH_TOKEN("REF-001", "Refresh token không hợp lệ"),
    GET_SHIPPING_FEE_FAILED("SHI-002", "Lấy thông tin phí vận chuyển thất bại");

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
