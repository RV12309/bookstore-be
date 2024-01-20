package com.hust.bookstore.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProvinceResponse {
    @JsonProperty("provinceId")
    @JsonAlias({"ProvinceID"})
    private int provinceId;

    @JsonProperty("provinceName")
    @JsonAlias({"ProvinceName"})
    private String provinceName;

    @JsonProperty("code")
    @JsonAlias({"code", "Code"})
    private String code;

    @JsonProperty("nameExtension")
    @JsonAlias({"NameExtension"})
    private List<String> nameExtension;

    @JsonProperty("countryId")
    @JsonAlias({"CountryID"})
    private int countryId;

}
