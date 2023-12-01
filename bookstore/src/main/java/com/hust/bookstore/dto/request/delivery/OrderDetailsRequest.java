package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetailsRequest {
    private String id;

    @JsonProperty("pick_name")
    private String pickName;

    @JsonProperty("pick_address")
    private String pickAddress;

    @JsonProperty("pick_province")
    private String pickProvince;

    @JsonProperty("pick_district")
    private String pickDistrict;

    @JsonProperty("pick_tel")
    private String pickTel;
    private String tel;
    private String name;
    private String address;
    private String province;
    private String district;

    @JsonProperty("is_freeship")
    private String isFreeship;

    @JsonProperty("pick_date")
    private String pickDate;

    @JsonProperty("pick_money")
    private double pickMoney;

    private String note;
    private double value;
    private List<Integer> tags;
}
