package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.AccountRequest;
import com.hust.bookstore.dto.request.UpdateUserRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.serrvice.UserService;
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

}
