package com.hust.bookstore.dto.request.delivery;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TokenRequest {
    private String username;
    private String password;
}
