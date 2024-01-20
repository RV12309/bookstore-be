package com.hust.bookstore.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PartnerBaseResponse<T> {
    private int code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return code == 200 || code == 201;
    }
}
