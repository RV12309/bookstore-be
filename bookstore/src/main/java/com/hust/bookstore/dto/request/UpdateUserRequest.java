package com.hust.bookstore.dto.request;

import com.hust.bookstore.enumration.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateUserRequest {
    @NotNull(message = "User id cannot be null.")
    private Long id;
    private String email;
    private String phone;
    private String name;
    private String avatarUrl;
    private LocalDateTime dob;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}
