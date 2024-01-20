package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.AccountRequest;
import com.hust.bookstore.dto.request.UpdateUserRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hust.bookstore.enumration.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/v1/sellers")
public class SellerController {
    private final UserService userService;

    public SellerController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Tạo tài khoản người bán")
    @PostMapping("/register")
    ResponseEntity<BaseResponse<Object>> createUser(@Valid @RequestBody AccountRequest request) {
        return userService.createAccount(request, UserType.SELLER);
    }
    @Operation(summary = "Cập nhật thông tin người bán")
    @PutMapping
    ResponseEntity<BaseResponse<Object>> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        userService.updateUser(request);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }


}
