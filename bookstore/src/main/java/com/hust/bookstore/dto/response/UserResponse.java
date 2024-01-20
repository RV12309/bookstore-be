package com.hust.bookstore.dto.response;

import com.hust.bookstore.dto.UserDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ApiResponse
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse extends UserDto {
    private String id;
    private String avatarUrl;
    private String username;
    private String createdAt;
    private String typeName;
    List<UserAddressResponse> userAddresses;

}
