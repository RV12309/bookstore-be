package com.hust.bookstore.serrvice.impl;

import com.hust.bookstore.common.Utils;
import com.hust.bookstore.dto.request.AccountRequest;
import com.hust.bookstore.dto.request.UserRequest;
import com.hust.bookstore.dto.request.VerifyAccountRequest;
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
import static com.hust.bookstore.enumration.ResponseCode.*;

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

    @Value("${account.verify.expired-time}")
    private Long verificationExpiredTime;

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
        String verificationCode = Utils.randomPassword();
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setVerificationCode(passwordEncoder.encode(verificationCode));
        LocalDateTime now = LocalDateTime.now();
        account.setVerificationExpiredAt(now.plusMinutes(verificationExpiredTime * 60));
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        account.setCreatedBy(email);
        account.setUpdatedBy(email);
        accountRepository.save(account);
        log.info("Create account successfully");
        sendNotificationEmail(account, verificationCode);
        return ResponseEntity.ok().build();
    }

    private void sendNotificationEmail(Account account, String verificationCode) {
        log.info("Send notification email to {}", account.getEmail());
        String verificationUrl = baseUrl + contextPath + verificationEndpoint;
        Context context = new Context();
        context.setVariable(USERNAME, account.getUsername());
        context.setVariable(ACTIVE_LINK, verificationUrl);
        context.setVariable(VERIFICATION_CODE, verificationCode);
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

    @Override
    public void verifyAccount(VerifyAccountRequest request) {
        log.info("Verify account with username: {}", request.getUsername());
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ACCOUNT_NOT_FOUND));
        log.info("Account found");

        if (Boolean.TRUE.equals(account.getIsVerified())) {
            throw new BusinessException(ACCOUNT_ALREADY_ACTIVE);
        }
        log.info("Account is not verified");

        if (account.getVerificationExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(VERIFICATION_CODE_EXPIRED);
        }
        log.info("Verification code is not expired");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(request.getVerificationCode(), account.getVerificationCode())) {
            throw new BusinessException(VERIFICATION_CODE_FAIL);
        }
        log.info("Verification code is correct");

        account.setIsEnabled(true);
        account.setIsVerified(true);
        account.setVerificationCode(null);
        accountRepository.save(account);
        log.info("Verify account successfully");

    }
}
