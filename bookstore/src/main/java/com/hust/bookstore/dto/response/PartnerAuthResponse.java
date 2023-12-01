package com.hust.bookstore.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PartnerAuthResponse {
    private String code;
    private String token;
}
