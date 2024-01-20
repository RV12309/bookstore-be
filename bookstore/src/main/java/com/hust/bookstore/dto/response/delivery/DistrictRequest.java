package com.hust.bookstore.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistrictRequest {
    @JsonProperty("province_id")
    private int provinceId;
}
