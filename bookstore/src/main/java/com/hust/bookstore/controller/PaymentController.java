package com.hust.bookstore.controller;

import com.hust.bookstore.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
public class PaymentController {
    private final UserService userService;

    public PaymentController(UserService userService) {
        this.userService = userService;
    }


}
