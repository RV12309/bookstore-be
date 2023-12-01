package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.AccountRequest;
import com.hust.bookstore.dto.request.UpdateUserRequest;
import com.hust.bookstore.dto.request.UserAddressRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.UserAddressResponse;
import com.hust.bookstore.dto.response.UserResponse;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hust.bookstore.enumration.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {
    private final UserService userService;

    public CustomerController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Tạo tài khoản khách hàng")
    @PostMapping("/register")
    ResponseEntity<BaseResponse<Object>> createUser(@Valid @RequestBody AccountRequest request) {
        return userService.createAccount(request, UserType.CUSTOMER);
    }

    @Operation(summary = "Cập nhật thông tin khách hàng")
    @PutMapping
    ResponseEntity<BaseResponse<Object>> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        userService.updateUser(request);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Cập nhật thông tin địa chỉ khách hàng")
    @PutMapping("/address/{id}")
    ResponseEntity<BaseResponse<UserAddressResponse>> updateUserAddress(@PathVariable Long id, @Valid @RequestBody UserAddressRequest request) {
        return ResponseEntity.ok(BaseResponse.<UserAddressResponse>builder()
                .code(SUCCESS.code())
                .message(SUCCESS.message())
                .data(userService.updateUserAddress(id, request)).build());
    }

    @Operation(summary = "Xóa địa chỉ khách hàng")
    @DeleteMapping("/address/{id}")
    ResponseEntity<BaseResponse<Object>> deleteUserAddress(@PathVariable Long id) {
        userService.deleteUserAddress(id);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Thêm địa chỉ khách hàng")
    @PostMapping("/address")
    ResponseEntity<BaseResponse<UserAddressResponse>> addUserAddress(@Valid @RequestBody UserAddressRequest request) {

        return ResponseEntity.ok(BaseResponse.<UserAddressResponse>builder()
                .code(SUCCESS.code())
                .message(SUCCESS.message())
                .data(userService.addUserAddress(request)).build());
    }

    @Operation(summary = "Lấy thông tin chi tiết khách hàng")
    @GetMapping
    ResponseEntity<BaseResponse<UserResponse>> getUserDetail() {
        return ResponseEntity.ok(BaseResponse.<UserResponse>builder()
                .code(SUCCESS.code())
                .message(SUCCESS.message())
                .data(userService.getUserDetail()).build());
    }
}
