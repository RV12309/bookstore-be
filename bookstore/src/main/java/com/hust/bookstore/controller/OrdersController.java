package com.hust.bookstore.controller;

import com.hust.bookstore.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
public class OrdersController {
    private final UserService userService;

    public OrdersController(UserService userService) {
        this.userService = userService;
    }


}
