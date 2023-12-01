package com.hust.bookstore.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistrictResponse {

    @JsonAlias({"DistrictID", "districtID"})
    @JsonProperty("districtId")
    private int districtId;

    @JsonAlias({"ProvinceID", "provinceID"})
    @JsonProperty("provinceId")
    private int provinceId;

    @JsonAlias({"DistrictName", "districtName"})
    @JsonProperty("districtName")
    private String districtName;

    @JsonAlias({"Code", "code"})
    @JsonProperty("code")
    private String code;

    @JsonAlias({"Type", "type"})
    @JsonProperty("type")
    private int type;

    @JsonAlias({"SupportType", "supportType"})
    @JsonProperty("supportType")
    private int supportType;
}
