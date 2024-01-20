package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelRequest {

    @NotBlank
    private String reason;
}
