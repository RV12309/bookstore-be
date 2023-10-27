package com.hust.bookstore.serrvice;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtProvider {
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    boolean validateToken(String authToken);

    String getUserNameFromJWT(String token);
}
