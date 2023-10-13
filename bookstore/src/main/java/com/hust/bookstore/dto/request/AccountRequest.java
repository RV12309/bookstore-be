package com.hust.bookstore.dto.request;

import com.hust.bookstore.enumration.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
