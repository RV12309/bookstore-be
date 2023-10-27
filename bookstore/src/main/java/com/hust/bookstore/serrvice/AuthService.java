package com.hust.bookstore.serrvice;

import com.hust.bookstore.dto.request.AuthRequest;
import com.hust.bookstore.entity.Account;

import java.util.Map;

public interface AuthService {
    Map<String, String> authRequest(AuthRequest request);

    Account getCurrentAccountLogin();
}
