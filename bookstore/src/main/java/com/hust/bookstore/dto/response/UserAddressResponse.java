package com.hust.bookstore.dto.response;

import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserAddressResponse {
    private String id;

    private String provinceId;

    private String districtId;

    private String wardCode;

    private String province;

    private String district;

    private String ward;
    private String firstAddress;
}
