package com.hust.bookstore.controller;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.OrderCancelRequest;
import com.hust.bookstore.dto.request.OrderStatusRequest;
import com.hust.bookstore.dto.request.PaymentStatusRequest;
import com.hust.bookstore.dto.request.SearchOrderRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.OrderResponse;
import com.hust.bookstore.dto.response.OrderStatisticResponse;
import com.hust.bookstore.dto.response.RevenueStatisticResponse;
import com.hust.bookstore.enumration.OrderStatisticType;
import com.hust.bookstore.enumration.OrderStatus;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.service.OrdersService;
import com.hust.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
    public ResponseEntity<BaseResponse<Object>> updateStatus(@PathVariable Long orderId, @Valid @RequestBody OrderStatusRequest request) {
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

    @Operation(summary = "Hủy đơn hàng")
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<BaseResponse<Object>> cancelOrder(@Valid @PathVariable Long orderId, @Valid @RequestBody OrderCancelRequest request) {
        ordersService.cancelOrder(orderId, request);
        return ResponseEntity.ok(BaseResponse.builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).build());
    }

    @Operation(summary = "Xác nhận đơn hàng")
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<BaseResponse<Object>> confirmOrder(@Valid @PathVariable Long orderId) {
        ordersService.updateStatus(orderId, OrderStatusRequest.builder().status(OrderStatus.PROCESSING).build());
        return ResponseEntity.ok(BaseResponse.builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).build());
    }


    @Operation(summary = "Thống kê đơn hàng theo tháng")
    @GetMapping("/statistic")
    public ResponseEntity<BaseResponse<List<OrderStatisticResponse>>> statisticOrder(@RequestParam(required = false) LocalDateTime from,
                                                                                     @RequestParam(required = false) LocalDateTime to,
                                                                                     @RequestParam OrderStatisticType type) {
        return ResponseEntity.ok(BaseResponse.<List<OrderStatisticResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(ordersService.statisticOrder(from, to, type)).build());
    }

    @Operation(summary = "Thống kê doanh thu theo tháng")
    @GetMapping("/statistic-revenue")
    public ResponseEntity<BaseResponse<List<RevenueStatisticResponse>>> statisticRevenue(@RequestParam(required = false) LocalDateTime from,
                                                                                         @RequestParam(required = false) LocalDateTime to,
                                                                                         @Valid @RequestParam OrderStatisticType type) {
        return ResponseEntity.ok(BaseResponse.<List<RevenueStatisticResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(ordersService.statisticRevenue(from, to, type)).build());
    }


}
