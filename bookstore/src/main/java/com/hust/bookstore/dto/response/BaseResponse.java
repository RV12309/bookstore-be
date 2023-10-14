package com.hust.bookstore.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BaseResponse<T> {
    private String code;
    private String message;
    private T data;
}
