package com.hust.bookstore.dto.request.delivery;

import com.hust.bookstore.entity.OrderDetails;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {
    private List<ProductRequest> products;
    private OrderDetails order;
}
