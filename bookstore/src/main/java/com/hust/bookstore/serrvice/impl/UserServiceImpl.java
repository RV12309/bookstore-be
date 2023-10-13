package com.hust.bookstore.serrvice.impl;

import com.hust.bookstore.dto.request.AccountRequest;
import com.hust.bookstore.dto.request.UserRequest;
import com.hust.bookstore.dto.response.UserResponse;
import com.hust.bookstore.entity.Account;
import com.hust.bookstore.enumration.MailTemplate;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.repository.AccountRepository;
import com.hust.bookstore.repository.UserRepository;
import com.hust.bookstore.serrvice.NotificationService;
import com.hust.bookstore.serrvice.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

import static com.hust.bookstore.common.Constants.*;
import static com.hust.bookstore.enumration.ResponseCode.EMAIL_EXISTED;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;

    @Value("${base-url}")
    private String baseUrl;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${account.active.link}")
    private String verificationEndpoint;

    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository,
                           AccountRepository accountRepository, NotificationService notificationService) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ResponseEntity<Object> createAccount(AccountRequest request) {
        String email = request.getEmail();
        log.info("Create account with email: {}", email);
        if (accountRepository.existsByEmail(email)) {
            throw new BusinessException(EMAIL_EXISTED);
        }
        Account account = Account.builder()
                .username(request.getUsername())
                .email(email)
                .type(UserType.USER)
                .isEnabled(false)
                .build();
        //Bcrypt password
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        LocalDateTime now = LocalDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        account.setCreatedBy(email);
        account.setUpdatedBy(email);
        accountRepository.save(account);
        log.info("Create account successfully");
        sendNotificationEmail(account);
        return ResponseEntity.ok().build();
    }

    private void sendNotificationEmail(Account account) {
        log.info("Send notification email to {}", account.getEmail());
        String verificationUrl = baseUrl + contextPath + verificationEndpoint;
        Context context = new Context();
        context.setVariable(USERNAME, account.getUsername());
        context.setVariable(ACTIVE_LINK, verificationUrl);
        notificationService.send(MailTemplate.NEW_ACCOUNT, context, account.getEmail());
        log.info("Send notification email successfully");
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UserRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<UserResponse> getUser(Long id) {
        return null;
    }
}
