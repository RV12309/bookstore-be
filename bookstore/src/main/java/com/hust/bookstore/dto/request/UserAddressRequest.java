package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserAddressRequest {
    @NotNull(message = "User id cannot be null.")
    private Long id;

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

    private boolean isDefault;
}
