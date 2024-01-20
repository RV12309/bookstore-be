package com.hust.bookstore.enumration;

public enum MailTemplate {
    NEW_ACCOUNT("new_account", "Đăng ký tài khoản thành công"),
    FORGOT_PASSWORD("forgot_password", "Quên mật khẩu"),
    CHANGE_PASSWORD("change_password", "Thay đổi mật khẩu"),
    NEW_ORDER("new_order", "Đặt hàng thành công - Mã đơn hàng: {0}"),
    ORDER_CONFIRM("order_confirm", "Xác nhận đơn hàng"),
    ORDER_CANCEL("order_cancel", "Hủy đơn hàng"),
    ORDER_DELIVER("order_deliver", "Giao hàng thành công"),
    ORDER_RETURN("order_return", "Trả hàng thành công"),
    ORDER_REFUND("order_refund", "Hoàn tiền thành công"),
    ORDER_REJECT("order_reject", "Từ chối đơn hàng");

    private final String code;
    private final String subject;

    MailTemplate(String code, String subject) {
        this.code = code;
        this.subject = subject;
    }

    public String getCode() {
        return code;
    }

    public String getSubject() {
        return subject;
    }

}
