package com.hust.bookstore.service.impl;

import com.hust.bookstore.common.Utils;
import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.UserResponse;
import com.hust.bookstore.dto.response.UserStatisticResponse;
import com.hust.bookstore.entity.Account;
import com.hust.bookstore.entity.User;
import com.hust.bookstore.enumration.MailTemplate;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.repository.AccountRepository;
import com.hust.bookstore.repository.UserRepository;
import com.hust.bookstore.service.NotificationService;
import com.hust.bookstore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<BaseResponse<Object>> createAccount(AccountRequest request, UserType userType) {
        String email = request.getEmail();
        log.info("Create account with email: {}", email);
        if (accountRepository.existsByEmail(email)) {
            throw new BusinessException(EMAIL_EXISTED);
        }
        Account account = Account.builder()
                .username(request.getUsername())
                .email(email)
                .type(userType)
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
        return ResponseEntity.ok(new BaseResponse<>(SUCCESS.code(), SUCCESS.message(), "Tạo tài khoản thành công"));
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
    public void updateUser(UpdateUserRequest request) {
        Long id = request.getId();
        log.info("Update user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        log.info("Found user with id: {}", id);
        modelMapper.map(request, user);
        userRepository.save(user);
        log.info("Update user successfully");
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Delete user with id: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ACCOUNT_NOT_FOUND));
        log.info("Found account with id: {}", id);
        account.setIsDeleted(true);
        accountRepository.save(account);
        log.info("Delete user successfully");
    }

    @Override
    public UserResponse getUser(Long id) {
        log.info("Get user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        log.info("Found user with id: {}", id);
        return modelMapper.map(user, UserResponse.class);
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

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Forgot password with email: {}", request.getEmail());
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ACCOUNT_NOT_FOUND));

        log.info("Found account with email: {}", request.getEmail());
        String newPassword = Utils.randomPassword();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        log.info("Update new password for account with email: {}", request.getEmail());

        Context context = new Context();
        context.setVariable(USERNAME, account.getUsername());
        context.setVariable(PASSWORD, newPassword);

        log.info("Send notification email to {}", account.getEmail());
        notificationService.send(MailTemplate.FORGOT_PASSWORD, context, account.getEmail());
        log.info("Send notification email successfully");
    }

    @Override
    public UserStatisticResponse statisticUser() {
        log.info("Statistic user");
        //find user group by type
        Map<UserType, Long> userStatistic = userRepository.statisticUser();
        //remove admin
        userStatistic.remove(UserType.ADMIN);
        //calculate total user
        Long totalUser = userStatistic.values().stream().reduce(0L, Long::sum);
        log.info("Statistic user successfully");
        return UserStatisticResponse.builder()
                .totalUser(totalUser)
                .userStatistic(userStatistic)
                .build();
    }

    @Override
    public Page<UserResponse> searchUsers(SearchUserRequest request) {
        log.info("Search user");
        List<String> sort = request.getSort();
        if (CollectionUtils.isEmpty(sort)) {
            sort = List.of("id");
        }
        Sort sortBy = Sort.by(sort.stream().map(Sort.Order::by).toList());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize()).withSort(sortBy);
        String username = StringUtils.isBlank(request.getUsername()) ? "%" : "%" + request.getUsername() + "%";
        String email = StringUtils.isBlank(request.getEmail()) ? "%" : "%" + request.getEmail() + "%";
        String phone = StringUtils.isBlank(request.getPhone()) ? "%" : "%" + request.getPhone() + "%";
        request.setUsername(username);
        request.setEmail(email);
        request.setPhone(phone);
        Page<User> users = userRepository.searchUsers(request, pageable);
        log.info("Found {} users", users.getTotalElements());
        log.info("Search user successfully");

        return users.map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Override
    public void lockUser(Long id) {
        log.info("Lock user with id: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ACCOUNT_NOT_FOUND));
        account.setIsEnabled(false);
        accountRepository.save(account);
        log.info("Lock user successfully");
    }

    @Override
    public void unlockUser(Long id) {
        log.info("Unlock user with id: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ACCOUNT_NOT_FOUND));
        account.setIsEnabled(true);
        accountRepository.save(account);
        log.info("Unlock user successfully");
    }
}
