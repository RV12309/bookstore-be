package com.hust.bookstore.controller;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.response.*;
import com.hust.bookstore.dto.response.delivery.*;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hust.bookstore.enumration.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/v1/global")
public class GlobalController {
    private final BooksService booksService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final DeliveryPartnerService deliveryPartnerService;
    private final OrdersService ordersService;


    public GlobalController(BooksService booksService, CategoryService categoryService, UserService userService,
                            ShoppingCartService shoppingCartService, DeliveryPartnerService deliveryPartnerService,
                            OrdersService ordersService) {
        this.booksService = booksService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.shoppingCartService = shoppingCartService;
        this.deliveryPartnerService = deliveryPartnerService;
        this.ordersService = ordersService;
    }


    @Operation(summary = "Lấy thông tin chi tiết sách theo mã ISBN")
    @GetMapping("/books/{isbn}")
    public ResponseEntity<BaseResponse<BookResponse>> getDetail(@PathVariable String isbn) {
        return ResponseEntity.ok(BaseResponse.<BookResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(booksService.getDetail(isbn)).build());
    }

    @Operation(summary = "Tìm kiếm sách")
    @PostMapping("/books/list")
    public ResponseEntity<BaseResponse<PageDto<BookResponse>>> searchBook(@Valid @RequestBody SearchBookRequest request) {
        return ResponseEntity.ok(BaseResponse.<PageDto<BookResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(booksService.searchBooks(request)).build());
    }

    @Operation(summary = "Lấy tất cả danh sách danh mục")
    @GetMapping("/categories/all")
    public ResponseEntity<BaseResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(BaseResponse.<List<CategoryResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(categoryService.getAll()).build());
    }

    @Operation(summary = "Quên mật khẩu")
    @PostMapping("/accounts/forgot-password")
    public ResponseEntity<BaseResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok(new BaseResponse<>(SUCCESS.code(), SUCCESS.message(), "Please check your email"));
    }

    @Operation(summary = "Lấy thông tin giỏ hàng")
    @GetMapping("/carts")
    public ResponseEntity<BaseResponse<CartResponse>> getCart(@RequestParam Long sessionId,
                                                              @RequestParam(required = false) Long refId) {
        return ResponseEntity.ok(BaseResponse.<CartResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(shoppingCartService.getCart(sessionId, refId)).build());
    }

    @Operation(summary = "Thêm sách vào giỏ hàng")
    @PutMapping("/carts")
    public ResponseEntity<BaseResponse<CartResponse>> addToCart(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(BaseResponse.<CartResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(shoppingCartService.addToCart(request)).build());
    }

    @Operation(summary = "Xóa giỏ hàng")
    @DeleteMapping("/carts")
    public ResponseEntity<BaseResponse<CartResponse>> deleteCartItem(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(BaseResponse.<CartResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(shoppingCartService.deleteCartItem(request)).build());
    }

    @Operation(summary = "Lấy thông tin địa chỉ tỉnh/thành phố")
    @GetMapping("/address/provinces")
    public ResponseEntity<BaseResponse<List<ProvinceResponse>>> getProvinces() {
        return ResponseEntity.ok(BaseResponse.<List<ProvinceResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(deliveryPartnerService.getProvinces()).build());
    }

    @Operation(summary = "Lấy thông tin địa chỉ quận/huyện")
    @GetMapping("/address/districts/{provinceId}")
    public ResponseEntity<BaseResponse<List<DistrictResponse>>> getDistricts(@PathVariable int provinceId) {
        return ResponseEntity.ok(BaseResponse.<List<DistrictResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(deliveryPartnerService.getDistricts(provinceId)).build());
    }

    @Operation(summary = "Lấy thông tin địa chỉ phường/xã")
    @GetMapping("/address/wards/{districtId}")
    public ResponseEntity<BaseResponse<List<WardResponse>>> getWards(@PathVariable int districtId) {
        return ResponseEntity.ok(BaseResponse.<List<WardResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(deliveryPartnerService.getWards(districtId)).build());
    }

    @Operation(summary = "Tạo mới đơn hàng")
    @PostMapping("/orders")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(BaseResponse.<OrderResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(ordersService.createOrder(request)).build());
    }

    @Operation(summary = "Lấy thông tin đơn hàng")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(BaseResponse.<OrderResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(ordersService.getOrder(orderId)).build());
    }


    @Operation(summary = "Lấy thông tin dich vụ vận chuyển")
    @PostMapping("/shipping-services")
    public ResponseEntity<BaseResponse<List<AvailableShippingServiceResponse>>> getShippingServices(@Valid @RequestBody ShippingServiceRequest request) {
        return ResponseEntity.ok(BaseResponse.<List<AvailableShippingServiceResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(deliveryPartnerService.getShippingServices(request)).build());
    }

    @Operation(summary = "Lấy thông tin phí vận chuyển")
    @PostMapping("/shipping-fee")
    public ResponseEntity<BaseResponse<ShippingFeeResponse>> getShippingFee(@Valid @RequestBody ShippingFeeRequest request) {
        return ResponseEntity.ok(BaseResponse.<ShippingFeeResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(deliveryPartnerService.getShippingFee(request)).build());
    }


}
