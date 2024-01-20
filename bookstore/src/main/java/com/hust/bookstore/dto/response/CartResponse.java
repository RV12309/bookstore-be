package com.hust.bookstore.dto.response;

import com.hust.bookstore.dto.CartItemDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartResponse {

    private String id;

    private String userId;

    private BigDecimal total;

    List<CartItemDto> items;

}
