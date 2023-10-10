package com.hust.bookstore.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
