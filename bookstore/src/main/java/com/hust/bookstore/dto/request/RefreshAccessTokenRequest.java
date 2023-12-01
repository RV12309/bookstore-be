package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshAccessTokenRequest {
    @NotBlank
    private String refreshToken;
}
