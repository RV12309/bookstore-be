package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GHNShippingRequest {
    @JsonAlias({"payment_type_id"})
    private int paymentTypeId;

    private String note;

    @JsonProperty("required_note")
    @JsonAlias({"required_note"})
    private String requiredNote;

    @JsonProperty("from_name")
    @JsonAlias({"from_name"})
    private String fromName;

    @JsonProperty("from_phone")
    @JsonAlias({"from_phone"})
    private String fromPhone;

    @JsonProperty("from_address")
    @JsonAlias({"from_address"})
    private String fromAddress;

    @JsonProperty("from_ward_name")
    @JsonAlias({"from_ward_name"})
    private String fromWardName;

    @JsonProperty("from_district_name")
    @JsonAlias({"from_district_name"})
    private String fromDistrictName;

    @JsonProperty("from_province_name")
    @JsonAlias({"from_province_name"})
    private String fromProvinceName;

    @JsonProperty("return_phone")
    @JsonAlias({"return_phone"})
    private String returnPhone;

    @JsonProperty("return_address")
    @JsonAlias({"return_address"})
    private String returnAddress;

    @JsonProperty("return_district_id")
    @JsonAlias({"return_district_id"})
    private Integer returnDistrictId;

    @JsonProperty("return_ward_code")
    @JsonAlias({"return_ward_code"})
    private String returnWardCode;

    @JsonProperty("client_order_code")
    @JsonAlias({"client_order_code"})
    private String clientOrderCode;

    @JsonProperty("to_name")
    @JsonAlias({"to_name"})
    private String toName;

    @JsonProperty("to_phone")
    @JsonAlias({"to_phone"})
    private String toPhone;

    @JsonProperty("to_address")
    @JsonAlias({"to_address"})
    private String toAddress;

    @JsonProperty("to_ward_code")
    @JsonAlias({"to_ward_code"})
    private String toWardCode;

    @JsonProperty("to_district_id")
    @JsonAlias({"to_district_id"})
    private Integer toDistrictId;

    @JsonProperty("cod_amount")
    @JsonAlias({"cod_amount"})
    private int codAmount;

    private String content;

    private int weight;

    private int length;

    private int width;

    private int height;

    @JsonProperty("pick_station_id")
    @JsonAlias({"pick_station_id"})
    private Integer pickStationId;

    @JsonProperty("deliver_station_id")
    @JsonAlias({"deliver_station_id"})
    private Integer deliverStationId;

    @JsonProperty("insurance_value")
    @JsonAlias({"insurance_value"})
    private int insuranceValue;

    @JsonProperty("service_id")
    @JsonAlias({"service_id"})
    private int serviceId;

    @JsonProperty("service_type_id")
    @JsonAlias({"service_type_id"})
    private int serviceTypeId;

    private String coupon;

    @JsonProperty("pick_shift")
    @JsonAlias({"pick_shift"})
    private List<Integer> pickShift;

    private List<GHNItemsRequest> items;

}