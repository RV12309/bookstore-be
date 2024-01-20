package com.hust.bookstore.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShippingFeeResponse {
    @JsonProperty("total")
    private int total;

    @JsonProperty("service_fee")
    @JsonAlias({"serviceFee", "service_fee"})
    private int serviceFee;

    @JsonProperty("insurance_fee")
    @JsonAlias({"insuranceFee", "insurance_fee"})
    private int insuranceFee;

    @JsonProperty("pick_station_fee")
    @JsonAlias({"pickStationFee", "pick_station_fee"})
    private int pickStationFee;

    @JsonProperty("coupon_value")
    @JsonAlias({"couponValue", "coupon_value"})
    private int couponValue;

    @JsonProperty("r2s_fee")
    @JsonAlias({"r2sFee", "r2s_fee"})
    private int r2sFee;

    @JsonProperty("return_again")
    @JsonAlias({"returnAgain", "return_again"})
    private int returnAgain;

    @JsonProperty("document_return")
    @JsonAlias({"documentReturn", "document_return"})
    private int documentReturn;

    @JsonProperty("double_check")
    @JsonAlias({"doubleCheck", "double_check"})
    private int doubleCheck;

    @JsonProperty("cod_fee")
    @JsonAlias({"codFee", "cod_fee"})
    private int codFee;

    @JsonProperty("pick_remote_areas_fee")
    @JsonAlias({"pickRemoteAreasFee", "pick_remote_areas_fee"})
    private int pickRemoteAreasFee;

    @JsonProperty("deliver_remote_areas_fee")
    @JsonAlias({"deliverRemoteAreasFee", "deliver_remote_areas_fee"})
    private int deliverRemoteAreasFee;

    @JsonProperty("cod_failed_fee")
    @JsonAlias({"codFailedFee", "cod_failed_fee"})
    private int codFailedFee;
}
