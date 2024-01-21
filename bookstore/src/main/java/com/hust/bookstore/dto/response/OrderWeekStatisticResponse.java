package com.hust.bookstore.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OrderWeekStatisticResponse {
    private String time;
    private Long totalOrder;
    private BigDecimal totalAmount;

}
