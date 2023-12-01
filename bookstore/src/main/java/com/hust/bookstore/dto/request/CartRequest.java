package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartRequest {
    @NotNull
    private Long sessionId;
}
