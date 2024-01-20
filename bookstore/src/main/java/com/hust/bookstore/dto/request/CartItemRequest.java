package com.hust.bookstore.dto.request;

import com.hust.bookstore.enumration.CartAction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItemRequest {
    @NotNull
    private Long sessionId;

    @NotNull
    private Long bookId;

    @NotNull
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Long quantity;

    @NotNull
    private CartAction action;

}
