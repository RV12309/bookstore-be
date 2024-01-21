package com.hust.bookstore.service;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.UserAddressResponse;
import com.hust.bookstore.dto.response.UserResponse;
import com.hust.bookstore.dto.response.UserStatisticResponse;
import com.hust.bookstore.enumration.UserType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<BaseResponse<Object>> createAccount(AccountRequest request, UserType userType);

    void updateUser(UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse getUser(Long id);

    void verifyAccount(VerifyAccountRequest request);


    void forgotPassword(ForgotPasswordRequest request);

    UserStatisticResponse statisticUser();

    PageDto<UserResponse> searchUsers(SearchUserRequest request);

    void lockUser(Long id);

    void unlockUser(Long id);

    UserAddressResponse updateUserAddress(Long id, UserAddressRequest request);

    void deleteUserAddress(Long id);

    UserAddressResponse addUserAddress(UserAddressRequest request);

    UserResponse getUserDetail();

    List<UserAddressResponse> getUserAddress();
}
