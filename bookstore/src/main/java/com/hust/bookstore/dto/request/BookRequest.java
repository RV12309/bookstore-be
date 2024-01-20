package com.hust.bookstore.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime publishDate;

    private Long numberOfPages;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Long price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Long quantity;

//    @NotNull(message = "Weight is required")
//    @Min(value = 1, message = "Weight must be greater than 0")
    private Long weight;

//    @NotNull(message = "Height is required")
//    @Min(value = 1, message = "Height must be greater than 0")
    private Long height;

//    @NotNull(message = "Width is required")
//    @Min(value = 1, message = "Width must be greater than 0")
    private Long width;

//    @NotNull(message = "Length is required")
//    @Min(value = 1, message = "Length must be greater than 0")
    private Long length;
}
