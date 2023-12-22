package com.hust.bookstore.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingFeeRequest {
    @JsonProperty("from_district_id")
    @JsonAlias("fromDistrictId")
    private Integer fromDistrictId;

    @JsonProperty("from_ward_code")
    @JsonAlias("fromWardCode")
    private String fromWardCode;

    @NotNull
    @JsonProperty("service_id")
    @JsonAlias("serviceId")
    private Integer serviceId;

    @JsonProperty("service_type_id")
    @JsonAlias("serviceTypeId")
    private Integer serviceTypeId;

    @NotNull
    @JsonProperty("to_district_id")
    @JsonAlias("toDistrictId")
    private Integer toDistrictId;

    @NotBlank
    @JsonAlias("to_ward_code")
    @JsonProperty("to_ward_code")
    private String toWardCode;

    private int height;
    private int length;
    private int weight;
    private int width;

    @JsonProperty("insurance_value")
    @JsonAlias("insuranceValue")
    private int insuranceValue;

    @JsonProperty("cod_failed_amount")
    @JsonAlias("codFailedAmount")
    private int codFailedAmount;

    private String coupon;
}
