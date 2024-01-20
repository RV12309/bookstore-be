package com.hust.bookstore.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderCallbackRequest{
    @JsonProperty("CODAmount")
    private int codAmount;

    @JsonProperty("CODTransferDate")
    private String codTransferDate;

    @JsonProperty("ClientOrderCode")
    private String clientOrderCode;

    @JsonProperty("ConvertedWeight")
    private int convertedWeight;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Fee")
    private Fee fee;

    @JsonProperty("Height")
    private int height;

    @JsonProperty("IsPartialReturn")
    private boolean isPartialReturn;

    @JsonProperty("Length")
    private int length;

    @JsonProperty("OrderCode")
    private String orderCode;

    @JsonProperty("PartialReturnCode")
    private String partialReturnCode;

    @JsonProperty("PaymentType")
    private int paymentType;

    @JsonProperty("Reason")
    private String reason;

    @JsonProperty("ReasonCode")
    private String reasonCode;

    @JsonProperty("ShopID")
    private int shopId;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Time")
    private String time;

    @JsonProperty("TotalFee")
    private int totalFee;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Warehouse")
    private String warehouse;

    @JsonProperty("Weight")
    private int weight;

    @JsonProperty("Width")
    private int width;

    // getters and setters
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Fee {
        @JsonProperty("CODFailedFee")
        private int codFailedFee;

        @JsonProperty("CODFee")
        private int codFee;

        @JsonProperty("Coupon")
        private int coupon;

        @JsonProperty("DeliverRemoteAreasFee")
        private int deliverRemoteAreasFee;

        @JsonProperty("DocumentReturn")
        private int documentReturn;

        @JsonProperty("DoubleCheck")
        private int doubleCheck;

        @JsonProperty("Insurance")
        private int insurance;

        @JsonProperty("MainService")
        private int mainService;

        @JsonProperty("PickRemoteAreasFee")
        private int pickRemoteAreasFee;

        @JsonProperty("R2S")
        private int r2s;

        @JsonProperty("Return")
        private int returnFee;

        @JsonProperty("StationDO")
        private int stationDo;

        @JsonProperty("StationPU")
        private int stationPu;

        @JsonProperty("Total")
        private int total;

        // getters and setters
    }
}
