package com.hust.bookstore.service;

import com.hust.bookstore.dto.request.AuthRequest;
import com.hust.bookstore.dto.request.RefreshAccessTokenRequest;
import com.hust.bookstore.entity.Account;

import java.util.Map;

public interface AuthService {
    Map<String, String> authRequest(AuthRequest request);

    Account getCurrentAccountLogin();

    Map<String, String> refreshToken(RefreshAccessTokenRequest request);
}
