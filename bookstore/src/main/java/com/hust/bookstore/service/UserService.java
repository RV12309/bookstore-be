package com.hust.bookstore.service;

import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.UserResponse;
import com.hust.bookstore.dto.response.UserStatisticResponse;
import com.hust.bookstore.enumration.UserType;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<BaseResponse<Object>> createAccount(AccountRequest request, UserType userType);

    void updateUser(UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse getUser(Long id);

    void verifyAccount(VerifyAccountRequest request);


    void forgotPassword(ForgotPasswordRequest request);

    UserStatisticResponse statisticUser();

    Page<UserResponse> searchUsers(SearchUserRequest request);

    void lockUser(Long id);

    void unlockUser(Long id);
}
