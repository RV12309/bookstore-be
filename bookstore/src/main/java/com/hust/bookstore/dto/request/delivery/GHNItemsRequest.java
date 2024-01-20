package com.hust.bookstore.dto.request.delivery;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class GHNItemsRequest {
    private String name;
    private String code;
    private int quantity;
    private int price;
    private int length;
    private int width;
    private int height;
    private int weight;

}
