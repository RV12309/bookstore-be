package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    @JsonAlias("order_code")
    private String orderCode;
    @JsonAlias("sort_code")
    private String sortCode;
    @JsonAlias("trans_type")
    private String transType;
    @JsonAlias("ward_encode")
    private String wardEncode;
    @JsonAlias("district_encode")
    private String districtEncode;

    @JsonAlias("fee")
    private Fee fee;
    @JsonAlias("total_fee")
    private int totalFee;
    @JsonAlias("expected_delivery_time")
    private LocalDateTime expectedDeliveryTime;

    // Constructors, getters, and setter
}
