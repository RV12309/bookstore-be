package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.AccountRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.serrvice.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
