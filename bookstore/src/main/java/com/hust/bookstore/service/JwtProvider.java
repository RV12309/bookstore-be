package com.hust.bookstore.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtProvider {
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    boolean validateToken(String authToken);

    String getUserNameFromJWT(String token);

    String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails);
}
