package com.hust.bookstore.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BaseResponse<T> {
    private String message;
    private String code;
    private T data;
}
