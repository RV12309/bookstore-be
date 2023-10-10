package com.hust.bookstore.serrvice;

import com.hust.bookstore.dto.request.AuthRequest;

import java.util.Map;

public interface AuthService {
    Map<String, String> authRequest(AuthRequest request);
}
