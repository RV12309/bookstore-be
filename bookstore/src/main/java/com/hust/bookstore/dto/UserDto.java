package com.hust.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    @NotBlank
    private String email;
    @NotBlank
    private String phone;
    @NotBlank
    private String name;
    private LocalDateTime dob;

}
