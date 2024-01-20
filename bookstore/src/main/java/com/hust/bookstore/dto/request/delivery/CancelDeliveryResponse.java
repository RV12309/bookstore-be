package com.hust.bookstore.dto.request.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelDeliveryResponse {
    @JsonAlias("order_code")
    private String orderCode;
    @JsonAlias("result")
    private boolean result;
    @JsonAlias("message")
    private String message;
}
