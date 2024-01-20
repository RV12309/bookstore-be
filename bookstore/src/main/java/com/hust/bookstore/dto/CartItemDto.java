package com.hust.bookstore.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItemDto {

    private String sessionId;

    private String bookId;

    private Long quantity;
    private String title;
    private BigDecimal price;
    private BigDecimal total;
    private String urlThumbnail;
    private String sellerId;
    private String sellerName;
}
