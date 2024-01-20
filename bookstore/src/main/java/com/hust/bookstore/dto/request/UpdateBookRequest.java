package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateBookRequest extends BookRequest{

    @NotBlank(message = "ISBN is required")
    private String isbn;

}
