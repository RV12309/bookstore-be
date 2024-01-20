package com.hust.bookstore.dto.request;

import com.hust.bookstore.enumration.UserType;
import lombok.*;

import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class SearchUserRequest {

    private String username;
    private String email;
    private String phone;
    private UserType type;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    private List<String> sort;

}
