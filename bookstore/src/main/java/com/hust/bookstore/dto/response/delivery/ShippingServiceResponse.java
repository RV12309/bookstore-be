package com.hust.bookstore.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShippingServiceResponse {

    @JsonProperty("service_id")
    private int serviceId;

    @JsonProperty("short_name")
    private String shortName;

    @JsonProperty("service_type_id")
    private int serviceTypeId;

    @JsonProperty("config_fee_id")
    private String configFeeId;

    @JsonProperty("extra_cost_id")
    private String extraCostId;

    @JsonProperty("standard_config_fee_id")
    private String standardConfigFeeId;

    @JsonProperty("standard_extra_cost_id")
    private String standardExtraCostId;
}
