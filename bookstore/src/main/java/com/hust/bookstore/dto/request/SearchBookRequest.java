package com.hust.bookstore.dto.request;

import lombok.*;

import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SearchBookRequest {
    private String title;
    private String author;
    private Long categoryId;

    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    private List<String> sort;
    private Long priceFrom;
    private Long priceTo;

}
