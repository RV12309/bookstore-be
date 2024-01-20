package com.hust.bookstore.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PageDto<T> {
    private Integer size;
    private Integer page;
    private Integer totalPages;
    private Long totalElements;
    private List<T> content;
}
