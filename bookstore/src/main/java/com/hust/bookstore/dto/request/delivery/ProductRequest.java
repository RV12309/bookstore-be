package com.hust.bookstore.dto.request.delivery;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductRequest {
    private String name;
    private double weight;
}
