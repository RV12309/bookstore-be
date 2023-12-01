package com.hust.bookstore.enumration;

public enum OrderStatus {
    PENDING("PENDING", "Chờ xử lý"),
    PROCESSING("PROCESSING", "Đang xử lý"),
    CANCELLED("CANCELLED", "Đã hủy"),
    COMPLETED("COMPLETED", "Đã hoàn thành"),
    SHIPPING("SHIPPING", "Đang giao");
    private String code;
    private String description;

    private OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
