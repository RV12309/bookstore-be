package com.hust.bookstore.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AvailableShippingServiceResponse {

    private int serviceId;

    private String shortName;

    private int serviceTypeId;
    private String configFeeId;

    private String extraCostId;

    private String standardConfigFeeId;

    private String standardExtraCostId;
}
