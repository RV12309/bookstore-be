package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyAccountRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String verificationCode;

}
