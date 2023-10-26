package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String author;

    private String description;

    @NotNull(message = "Category id is required")
    private List<Long> categoryIds;

    private List<String> imagesUrls;

    private String urlThumbnail;

    @NotBlank(message = "Image cover is required")
    private String urlImageCover;

    private String publisher;

    private String publishDate;

    private String language;

    private Long numberOfPages;

    @NotNull(message = "Price is required")
    private Long price;

    @NotNull(message = "Quantity is required")
    private Long quantity;
}
