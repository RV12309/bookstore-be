package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateBookRequest extends BookRequest{

    @NotNull(message = "Id is required")
    private Long id;

    @NotBlank(message = "ISBN is required")
    private String isbn;

}
