package com.hust.bookstore.dto;

import com.hust.bookstore.enumration.Gender;
import com.hust.bookstore.enumration.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    @NotBlank
    private String email;
    @NotBlank
    private String phone;
    @NotBlank
    private String name;
    private String dob;

    private Gender gender;

    private UserType type;

    private Long provinceId;
    private Long districtId;
    private String wardCode;
    private String province;
    private String district;
    private String ward;
    private String firstAddress;

}
