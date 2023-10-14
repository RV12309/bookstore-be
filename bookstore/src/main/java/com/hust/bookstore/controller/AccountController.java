package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.VerifyAccountRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.serrvice.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hust.bookstore.enumration.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/verification")
    public ResponseEntity<BaseResponse<String>> verification(@Valid @RequestBody VerifyAccountRequest request) {
        userService.verifyAccount(request);
        return ResponseEntity.ok(new BaseResponse<>(SUCCESS.code(), SUCCESS.message(), "Verify successfully"));
    }
}
