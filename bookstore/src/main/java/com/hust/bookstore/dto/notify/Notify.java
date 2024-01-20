package com.hust.bookstore.dto.notify;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Notify<T> {

    private T data;

    private String appId;

}
