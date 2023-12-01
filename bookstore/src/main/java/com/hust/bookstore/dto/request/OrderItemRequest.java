package com.hust.bookstore.dto.request;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemRequest {

    private Long bookId;
    private String title;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal total;
}
