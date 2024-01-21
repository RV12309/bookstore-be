package com.hust.bookstore.service.impl;

import com.hust.bookstore.common.Utils;
import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.response.*;
import com.hust.bookstore.entity.Account;
import com.hust.bookstore.entity.User;
import com.hust.bookstore.entity.UserAddress;
import com.hust.bookstore.enumration.MailTemplate;
import com.hust.bookstore.enumration.UserType;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.repository.projection.StatUserProjection;
import com.hust.bookstore.service.AuthService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hust.bookstore.common.Constants.*;
import static com.hust.bookstore.enumration.ResponseCode.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class UserServiceImpl extends BusinessHelper implements UserService {

    @Value("${base-url}")
    private String baseUrl;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${account.active.link}")
    private String verificationEndpoint;

    @Value("${account.verify.expired-time}")
    private Long verificationExpiredTime;

    public UserServiceImpl(BookRepository bookRepository, CartRepository cartRepository, CartItemRepository cartItemRepository,
                           PaymentRepository paymentRepository, DeliveryPartnerConfigRepository deliveryPartnerConfigRepo,
                           StoreDeliveryPartnerRepository storeDeliveryPartnerRepo, UserRepository userRepository,
                           OrderDetailRepository orderDetailsRepository, OrderItemsRepository orderItemsRepository,
                           CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository,
                           AccountRepository accountRepository, AuthService authService, BookImageRepository bookImageRepository,
                           ModelMapper modelMapper, NotificationService notificationService,
                           UserAddressRepository addressRepository, DeliveryDetailRepository deliveryDetailRepository) {
        super(bookRepository, cartRepository, cartItemRepository, paymentRepository,
                deliveryPartnerConfigRepo, storeDeliveryPartnerRepo, userRepository,
                orderDetailsRepository, orderItemsRepository, categoryRepository,
                bookCategoryRepository, accountRepository, authService, bookImageRepository, modelMapper,
                notificationService, addressRepository, deliveryDetailRepository);
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
        String verificationUrl = baseUrl + verificationEndpoint;
        Context context = new Context();
        context.setVariable(USERNAME, account.getUsername());
        context.setVariable(ACTIVE_LINK, verificationUrl);
        context.setVariable(VERIFICATION_CODE, verificationCode);
        notificationService.send(MailTemplate.NEW_ACCOUNT, context, account.getEmail());
        log.info("Send notification email successfully");
    }

    @Override
    @Transactional
    public void updateUser(UpdateUserRequest request) {
        Long id = request.getId();
        log.info("Update user with id: {}", id);
        Account account = authService.getCurrentAccountLogin();
        Account currentAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new BusinessException(ACCOUNT_NOT_FOUND));
//        if (non(currentAccount.getUserId().equals(id))) {
//            throw new BusinessException(USER_NOT_MATCH);
//        }
        User user;
        if (nonNull(account.getUserId())) {
            user = userRepository.findById(id).orElse(new User());
        } else {
            user = new User();
        }

        log.info("Found user with id: {}", id);
        modelMapper.map(request, user);
        user.setAccountId(currentAccount.getId());
        user.setType(currentAccount.getType());
        userRepository.save(user);
        currentAccount.setUserId(user.getId());
        accountRepository.save(currentAccount);
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
        List<StatUserProjection> userStatistic = userRepository.statisticUser();
        log.info("Found {} users", userStatistic.size());
        //filter user type
        List<CountUserStatisticResponse>
                userStatisticRes = new ArrayList<>(userStatistic.stream().map(user -> CountUserStatisticResponse.builder()
                .type(user.getType())
                .count(user.getCount())
                .build()).toList());
        log.info("Found {} ", userStatisticRes);
        //remove admin
        userStatisticRes.removeIf(user -> user.getType().equals(UserType.ADMIN));
        //calculate total user
        Long totalUser = userStatistic.stream().mapToLong(StatUserProjection::getCount).sum();
        log.info("Statistic user successfully");
        return UserStatisticResponse.builder()
                .totalUser(totalUser)
                .userStatistic(userStatisticRes)
                .build();
    }

    @Override
    public PageDto<UserResponse> searchUsers(SearchUserRequest request) {
        log.info("Search user with request: {}", request);
        List<String> sort = request.getSort();
        if (CollectionUtils.isEmpty(sort)) {
            sort = List.of("id");
        }
        Sort sortBy = Sort.by(sort.stream().map(Sort.Order::by).toList());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        String name = StringUtils.isBlank(request.getUsername()) ? "%" : "%" + request.getUsername() + "%";
        String email = StringUtils.isBlank(request.getEmail()) ? "%" : "%" + request.getEmail() + "%";
        String phone = StringUtils.isBlank(request.getPhone()) ? "%" : "%" + request.getPhone() + "%";
        UserType type = isNull(request.getType()) ? null : request.getType();
        List<UserType> types = new ArrayList<>();
        if (nonNull(type) && type.equals(UserType.CUSTOMER)) {
            types.addAll(List.of(UserType.CUSTOMER, UserType.GUEST));
        } else if (nonNull(type)) {
            types.add(type);
        }
        Page<User> users = userRepository.searchUsers(types, pageable);
        log.info("Found {} users", users.getTotalElements());
        log.info("Search user successfully");
        Map<Long, String> userMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(users.getContent())) {
            List<Long> userIds = users.getContent().stream().map(User::getId).toList();
            List<Account> accounts = accountRepository.findAllByUserIdIn(userIds);
            userMap = accounts.stream().collect(HashMap::new, (m, v) -> m.put(v.getUserId(), v.getUsername()), HashMap::putAll);
        }
        Map<Long, String> finalUserMap = userMap;
        List<UserResponse> userResponses = users.getContent().stream().map(user -> {
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            userResponse.setUsername(finalUserMap.get(user.getId()));
            userResponse.setTypeName(user.getType().getName());
            userResponse.setCreatedAt(user.getCreatedAt().format(Utils.formatter));
            return userResponse;
        }).toList();
        return PageDto.<UserResponse>builder()
                .content(userResponses)
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .page(users.getNumber())
                .size(users.getSize())
                .build();

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

    @Override
    public UserAddressResponse updateUserAddress(Long id, UserAddressRequest request) {
        log.info("Start update user address with id: {}", id);
        Account account = authService.getCurrentAccountLogin();
        User user = checkExistUser(account.getId());
        UserAddress userAddress = checkExistUserAddress(id);

        if (!user.getId().equals(userAddress.getUserId())) {
            throw new BusinessException(USER_ADDRESS_NOT_MATCH);
        }

        modelMapper.map(request, userAddress);
        if (Boolean.TRUE.equals(request.isDefault())) {
            addressRepository.updateDefaultAddress(user.getId());
        }
        UserAddress address = addressRepository.save(userAddress);
        log.info("Update user address successfully");
        return modelMapper.map(address, UserAddressResponse.class);
    }

    @Override
    public void deleteUserAddress(Long id) {
        log.info("Start delete user address with id: {}", id);
        Account account = authService.getCurrentAccountLogin();
        User user = checkExistUser(account.getId());
        UserAddress userAddress = checkExistUserAddress(id);
        if (!user.getId().equals(userAddress.getUserId())) {
            throw new BusinessException(USER_ADDRESS_NOT_MATCH);
        }

        if (Boolean.TRUE.equals(userAddress.isDefault())) {
            throw new BusinessException(USER_ADDRESS_DEFAULT);
        }
        addressRepository.delete(userAddress);
        log.info("Delete user address successfully");
    }

    @Override
    public UserAddressResponse addUserAddress(UserAddressRequest request) {
        log.info("Start add user address");
        Account account = authService.getCurrentAccountLogin();
        User user = checkExistUser(account.getId());
        UserAddress userAddress = modelMapper.map(request, UserAddress.class);
        userAddress.setUserId(user.getId());
        if (Boolean.TRUE.equals(request.isDefault())) {
            addressRepository.updateDefaultAddress(user.getId());
        }
        UserAddress address = addressRepository.save(userAddress);
        log.info("Add user address successfully");
        return modelMapper.map(address, UserAddressResponse.class);
    }

    @Override
    public UserResponse getUserDetail() {
        log.info("Get user detail");
        Account account = authService.getCurrentAccountLogin();
        User user;

        if (isNull(account.getUserId())) {
            log.info("User info not found, create new");
            User newUser = new User();
            newUser.setAccountId(account.getId());
            user = userRepository.save(newUser);
        } else {
            user = userRepository.findById(account.getUserId()).orElse(null);
            if (isNull(user)) {
                log.info("User info not found, create new");
                User newUser = new User();
                newUser.setAccountId(account.getId());
                user = userRepository.save(newUser);
            }
        }

        List<UserAddress> userAddresses = addressRepository.findByUserId(user.getId());
        List<UserAddressResponse> userAddressesRes = new ArrayList<>();
        if (non(CollectionUtils.isEmpty(userAddresses))) {
            log.info("User address not found, create new");
            userAddressesRes = userAddresses.stream().map(userAddress
                    -> modelMapper.map(userAddress, UserAddressResponse.class)).toList();
        }

        log.info("Get user detail successfully");
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        LocalDateTime dob = user.getDob();
        //convert dob to string
        if (nonNull(dob)) {
            userResponse.setDob(dob.format(Utils.formatter));
        }

        userResponse.setTypeName(account.getType().getName());
        userResponse.setUserAddresses(userAddressesRes);
        return modelMapper.map(user, UserResponse.class);

    }

    @Override
    public List<UserAddressResponse> getUserAddress() {
        log.info("Get user address");
        Account account = authService.getCurrentAccountLogin();
        User user = checkExistUser(account.getId());
        List<UserAddress> userAddresses = addressRepository.findByUserId(user.getId());
        log.info("Get user address successfully");
        return userAddresses.stream().map(userAddress -> modelMapper.map(userAddress, UserAddressResponse.class)).toList();

    }
}
