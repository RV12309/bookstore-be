package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GHNShopInfoRequest {
    private String name;

    @JsonProperty("district_id")
    @JsonAlias("district_id")
    private Integer districtId;

    @JsonProperty("ward_code")
    @JsonAlias("ward_code")
    private String wardCode;
    private String phone;
    private String address;
}
