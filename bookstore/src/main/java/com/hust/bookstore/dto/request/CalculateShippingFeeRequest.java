package com.hust.bookstore.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateShippingFeeRequest {


    @NotNull
    @JsonProperty("service_id")
    @JsonAlias("serviceId")
    private Integer serviceId;

    @NotNull
    @JsonProperty("to_district_id")
    @JsonAlias("toDistrictId")
    private Integer toDistrictId;

    @NotBlank
    @JsonAlias("to_ward_code")
    @JsonProperty("to_ward_code")
    private String toWardCode;

    private Long sessionId;
}
