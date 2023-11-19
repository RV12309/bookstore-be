package com.hust.bookstore.enumration;

public enum DeliveryStatus {
    PENDING("PENDING", "Đang chờ"),
    SHIPPING("SHIPPING", "Đang giao"),
    DELIVERED("DELIVERED", "Đã giao"),
    CANCELLED("CANCELLED", "Đã hủy")

    ;

    private String code;
    private String description;
    private DeliveryStatus(String code, String description) {
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
