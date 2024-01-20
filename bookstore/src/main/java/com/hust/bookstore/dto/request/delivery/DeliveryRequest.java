package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequest {
    @JsonProperty("payment_type_id")
    private int paymentTypeId;
    @JsonProperty("note")
    private String note;
    @JsonProperty("required_note")
    private String requiredNote;

    @JsonProperty("from_name")
    private String fromName;

    @JsonProperty("from_phone")
    private String fromPhone;
    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("from_ward_code")
    private String fromWardCode;


    @JsonProperty("from_ward_name")
    private String fromWardName;

    @JsonProperty("from_district_name")
    private String fromDistrictName;

    @JsonProperty("from_province_name")
    private String fromProvinceName;

    @JsonProperty("return_phone")
    private String returnPhone;
    @JsonProperty("return_address")
    private String returnAddress;
    @JsonProperty("return_district_id")
    private Long returnDistrictId;
    @JsonProperty("return_ward_code")
    private String returnWardCode;
    @JsonProperty("client_order_code")
    private String clientOrderCode;
    @JsonProperty("to_name")
    private String toName;
    @JsonProperty("to_phone")
    private String toPhone;
    @JsonProperty("to_address")
    private String toAddress;
    @JsonProperty("to_ward_code")
    private String toWardCode;
    @JsonProperty("to_district_id")
    private Long toDistrictId;
    @JsonProperty("cod_amount")
    private int codAmount;
    @JsonProperty("content")
    private String content;
    @JsonProperty("weight")
    private int weight;
    @JsonProperty("length")
    private int length;
    @JsonProperty("width")
    private int width;
    @JsonProperty("height")
    private int height;
    @JsonProperty("pick_station_id")
    private Long pickStationId;
    @JsonProperty("deliver_station_id")
    private Long deliverStationId;
    @JsonProperty("insurance_value")
    private int insuranceValue;
    @JsonProperty("service_id")
    private int serviceId;
    @JsonProperty("service_type_id")
    private int serviceTypeId;
    @JsonProperty("coupon")
    private String coupon;
    @JsonProperty("pick_shift")
    private List<Integer> pickShift;

    @JsonProperty("items")
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        @JsonProperty("name")
        private String name;
        @JsonProperty("code")
        private String code;
        @JsonProperty("quantity")
        private int quantity;
        @JsonProperty("price")
        private int price;
        @JsonProperty("length")
        private int length;
        @JsonProperty("width")
        private int width;
        @JsonProperty("height")
        private int height;
        @JsonProperty("weight")
        private int weight;
        @JsonProperty("category")
        private Category category;

        // Constructors, getters, and setters
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Category {
        @JsonProperty("level1")
        private String level1;

    }
}
