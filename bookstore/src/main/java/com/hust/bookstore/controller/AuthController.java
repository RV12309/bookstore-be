package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.AuthRequest;
import com.hust.bookstore.serrvice.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthRequest request) {
        log.info("Authenticating user {}.", request.getUsername());
        Map<String, String> userRegistrationResponse = authService.authRequest(request);
        log.info("User authenticated.");
        return ResponseEntity.ok(userRegistrationResponse);
    }
}
