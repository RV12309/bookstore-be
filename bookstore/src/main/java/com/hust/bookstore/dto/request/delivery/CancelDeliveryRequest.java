package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelDeliveryRequest {
    @JsonProperty("order_codes")
    private List<String> orderCodes;
}
