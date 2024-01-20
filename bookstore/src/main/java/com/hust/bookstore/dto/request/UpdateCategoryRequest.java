package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateCategoryRequest extends CategoryRequest {
    @NotNull(message = "Id is required")
    private Long id;
}
