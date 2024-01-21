package com.hust.bookstore.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticResponse {
    private Long totalUser;
    private List<CountUserStatisticResponse> userStatistic;
}
