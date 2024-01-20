package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fee {
    @JsonProperty("main_service")
    private int mainService;
    @JsonProperty("insurance")
    private int insurance;
    @JsonProperty("cod_fee")
    private int codFee;
    @JsonProperty("station_do")
    private int stationDo;
    @JsonProperty("station_pu")
    private int stationPu;
    @JsonProperty("return_fee")
    private int returnFee;
    @JsonProperty("r2s_fee")
    private int r2sFee;
    @JsonProperty("return_again_fee")
    private int returnAgainFee;
    @JsonProperty("coupon_fee")
    private int couponFee;
    @JsonProperty("document_return_fee")
    private int documentReturnFee;
    @JsonProperty("double_check_fee")
    private int doubleCheckFee;
    @JsonProperty("double_check_deliver_fee")
    private int doubleCheckDeliverFee;
    @JsonProperty("pick_remote_areas_fee")
    private int pickRemoteAreasFee;
    @JsonProperty("deliver_remote_areas_fee")
    private int deliverRemoteAreasFee;
    @JsonProperty("pick_remote_areas_fee_return")
    private int pickRemoteAreasFeeReturn;
    @JsonProperty("deliver_remote_areas_fee_return")
    private int deliverRemoteAreasFeeReturn;
    @JsonProperty("cod_failed_fee")
    private int codFailedFee;

    // Constructors, getters, and setters
}