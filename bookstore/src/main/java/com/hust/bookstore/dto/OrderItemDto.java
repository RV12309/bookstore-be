package com.hust.bookstore.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDto {

    private String bookId;
    private String title;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal total;
    private String urlThumbnail;
}
