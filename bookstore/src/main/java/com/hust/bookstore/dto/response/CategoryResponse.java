package com.hust.bookstore.dto.response;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.*;

@Getter
@Setter
@ApiResponse
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
}
