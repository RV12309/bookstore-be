package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableShippingServiceRequest {
    @JsonProperty("shop_id")
    private Integer shopId;

    @JsonProperty("from_district")
    private Integer fromDistrict;

    @JsonProperty("to_district")
    private Integer toDistrict;
}
