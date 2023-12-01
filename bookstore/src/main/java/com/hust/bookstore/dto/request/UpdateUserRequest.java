package com.hust.bookstore.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hust.bookstore.enumration.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateUserRequest {
    @NotNull(message = "User id cannot be null.")
    private Long id;
    @NotBlank(message = "Email cannot be blank.")
    private String email;
    @NotBlank(message = "Phone cannot be blank.")
    private String phone;
    @NotBlank(message = "Name cannot be blank.")
    private String name;

    private String avatarUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "Province id cannot be null.")
    private Long provinceId;

    @NotNull(message = "District id cannot be null.")
    private Long districtId;

    @NotNull(message = "Ward code cannot be null.")
    private Long wardCode;

    @NotBlank(message = "Province cannot be blank.")
    private String province;
    @NotBlank(message = "District cannot be blank.")
    private String district;
    @NotBlank(message = "Ward cannot be blank.")
    private String ward;
    private String firstAddress;
}
