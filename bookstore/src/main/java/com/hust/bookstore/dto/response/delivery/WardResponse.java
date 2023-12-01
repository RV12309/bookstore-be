package com.hust.bookstore.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WardResponse {

    @JsonAlias({"WardCode"})
    @JsonProperty("wardCode")
    private int wardCode;

    @JsonAlias({"WardName"})
    @JsonProperty("wardName")
    private String wardName;

    @JsonAlias({"DistrictID"})
    @JsonProperty("districtId")
    private int districtId;
}
