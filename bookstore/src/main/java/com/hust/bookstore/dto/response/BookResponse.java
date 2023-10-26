package com.hust.bookstore.dto.response;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.*;

@Getter
@Setter
@ApiResponse
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Long id;

    private String isbn;

    private String title;

    private String author;

    private String description;

    private String urlThumbnail;

    private String urlImageCover;

    private String publisher;

    private String publishDate;

    private String language;

    private Long numberOfPages;

    private Long price;

    private Long quantity;
}
