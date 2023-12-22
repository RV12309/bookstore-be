package com.hust.bookstore.controller;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.OrderStatusRequest;
import com.hust.bookstore.dto.request.PaymentStatusRequest;
import com.hust.bookstore.dto.request.SearchOrderRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.OrderResponse;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.service.OrdersService;
import com.hust.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
public class OrdersController {
    private final UserService userService;
    private final OrdersService ordersService;

    public OrdersController(UserService userService, OrdersService ordersService) {
        this.userService = userService;
        this.ordersService = ordersService;
    }

    @Operation(summary = "Cập nhật trạng thái đơn hàng, dành cho seller")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<BaseResponse<Object>> updateStatus(@PathVariable Long orderId, @RequestBody OrderStatusRequest request) {
        ordersService.updateStatus(orderId, request);
        return ResponseEntity.ok(BaseResponse.builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).build());
    }

    @Operation(summary = "Cập nhật trạng thái thanh toán đơn hàng, dành cho seller")
    @PatchMapping("/{orderId}/payment-status")
    public ResponseEntity<BaseResponse<Object>> updatePaymentStatus(@PathVariable Long orderId, @RequestBody PaymentStatusRequest request) {
        ordersService.updatePaymentStatus(orderId, request);
        return ResponseEntity.ok(BaseResponse.builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).build());
    }

    @Operation(summary = "Lấy thông tin đơn hàng theo id")
    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(BaseResponse.<OrderResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(ordersService.getOrder(orderId)).build());
    }

    @Operation(summary = "Lấy danh sách đơn hàng của khách hàng")
    @PostMapping
    public ResponseEntity<BaseResponse<PageDto<OrderResponse>>> getOrders(@Valid @RequestBody SearchOrderRequest request) {
        return ResponseEntity.ok(BaseResponse.<PageDto<OrderResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(ordersService.getOrders(request)).build());
    }

}
