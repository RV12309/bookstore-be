package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CategoryRequest {
    @NotBlank(message = "Code cannot be blank")
    private String name;
    private String description;
}
