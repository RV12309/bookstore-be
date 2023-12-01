package com.hust.bookstore.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingFeeRequest {
    @JsonProperty("from_district_id")
    private int fromDistrictId;

    @JsonAlias("from_ward_code")
    private String fromWardCode;

    @JsonProperty("service_id")
    private int serviceId;

    @JsonProperty("service_type_id")
    private Integer serviceTypeId;

    @JsonProperty("to_district_id")
    private int toDistrictId;

    @JsonAlias("to_ward_code")
    private String toWardCode;

    private int height;
    private int length;
    private int weight;
    private int width;

    @JsonProperty("insurance_value")
    private int insuranceValue;

    @JsonProperty("cod_failed_amount")
    private int codFailedAmount;

    private String coupon;
}
