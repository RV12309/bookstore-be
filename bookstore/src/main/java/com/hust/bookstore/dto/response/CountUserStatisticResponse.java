package com.hust.bookstore.dto.response;

import com.hust.bookstore.enumration.UserType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CountUserStatisticResponse {
    private UserType type;
    private Long count;
}
