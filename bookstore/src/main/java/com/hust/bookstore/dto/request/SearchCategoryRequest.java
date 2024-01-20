package com.hust.bookstore.dto.request;

import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SearchCategoryRequest {
    private String name;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 10;
}
