package com.hust.bookstore.serrvice.impl;

import com.hust.bookstore.common.Constants;
import com.hust.bookstore.dto.request.AuthRequest;
import com.hust.bookstore.serrvice.AuthService;
import com.hust.bookstore.serrvice.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.hust.bookstore.common.Constants.TOKEN;
import static com.hust.bookstore.common.Constants.USERNAME;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public Map<String, String> authRequest(AuthRequest request) {
        log.info("Start authenticate user {}.", request.getUsername());
        final Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername()
                , request.getPassword()));
        final UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
        log.info("User {} is authenticated.", userDetails.getUsername());
        return getToken(userDetails);
    }

    public Map<String, String> getToken(UserDetails userDetails) {
        log.info("Generating token for user {}.", userDetails.getUsername());
        final Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.ROLE, roles);
        claims.put(USERNAME, userDetails.getUsername());
        final String token = jwtService.generateToken(claims, userDetails);
        log.info("Token generated.");
        return Map.of(TOKEN, token);
    }
}
