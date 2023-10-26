package com.hust.bookstore.serrvice;

import com.hust.bookstore.dto.request.AccountRequest;
import com.hust.bookstore.dto.request.ForgotPasswordRequest;
import com.hust.bookstore.dto.request.UserRequest;
import com.hust.bookstore.dto.request.VerifyAccountRequest;
import com.hust.bookstore.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<Object> createAccount(AccountRequest request);

    ResponseEntity<UserResponse> updateUser(UserRequest request);

    ResponseEntity<Void> deleteUser(Long id);

    ResponseEntity<UserResponse> getUser(Long id);

    void verifyAccount(VerifyAccountRequest request);


    void forgotPassword(ForgotPasswordRequest request);
}
