package com.hust.bookstore.dto.response;

import com.hust.bookstore.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse extends UserDto {
    private String id;
    private String avatarUrl;
}
