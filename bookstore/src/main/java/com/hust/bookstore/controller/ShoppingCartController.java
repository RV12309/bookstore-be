package com.hust.bookstore.controller;

import com.hust.bookstore.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/shopping-cart")
public class ShoppingCartController {
    private final UserService userService;

    public ShoppingCartController(UserService userService) {
        this.userService = userService;
    }


}
