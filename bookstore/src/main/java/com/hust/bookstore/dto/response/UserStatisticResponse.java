package com.hust.bookstore.dto.response;

import com.hust.bookstore.enumration.UserType;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticResponse {
    private Long totalUser;
    private Map<UserType, Long> userStatistic;
}
