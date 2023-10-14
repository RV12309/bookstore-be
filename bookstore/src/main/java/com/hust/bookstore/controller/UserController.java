package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.AccountRequest;
import com.hust.bookstore.serrvice.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    ResponseEntity<Object> createUser(@Valid @RequestBody AccountRequest request) {
        return userService.createAccount(request);
    }
}
