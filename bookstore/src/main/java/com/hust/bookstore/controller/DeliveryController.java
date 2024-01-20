package com.hust.bookstore.controller;

import com.hust.bookstore.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/delivery")
public class DeliveryController {
    private final UserService userService;

    public DeliveryController(UserService userService) {
        this.userService = userService;
    }


}
