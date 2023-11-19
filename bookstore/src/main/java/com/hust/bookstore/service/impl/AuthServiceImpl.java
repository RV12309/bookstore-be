package com.hust.bookstore.service.impl;

import com.hust.bookstore.common.Constants;
import com.hust.bookstore.dto.CustomUserDetail;
import com.hust.bookstore.dto.request.AuthRequest;
import com.hust.bookstore.entity.Account;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.repository.AccountRepository;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.hust.bookstore.common.Constants.*;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    private final AccountRepository accountRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtProvider jwtProvider, AccountRepository accountRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.accountRepository = accountRepository;
    }

    @Override
    public Map<String, String> authRequest(AuthRequest request) {
        log.info("Start authenticate user {}.", request.getUsername());
        try {
            final Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername()
                    , request.getPassword()));
            final UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
            log.info("User {} is authenticated.", userDetails.getUsername());
            return getToken(userDetails);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.info("Username or password is invalid");
            throw new BusinessException(ResponseCode.INVALID_USERNAME_OR_PASSWORD);
        } catch (LockedException e) {
            log.info("User is not verified");
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_VERIFY);
        } catch (DisabledException e) {
            log.info("User is disabled");
            throw new BusinessException(ResponseCode.ACCOUNT_LOCKED);
        }

    }

    public Map<String, String> getToken(UserDetails userDetails) {
        log.info("Generating token for user {}.", userDetails.getUsername());
        final Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.ROLE, roles);
        claims.put(USERNAME, userDetails.getUsername());
        CustomUserDetail user = (CustomUserDetail) userDetails;
        claims.put(ACCOUNT, user.getAccount());
        final String token = jwtProvider.generateToken(claims, userDetails);
        log.info("Token generated.");
        return Map.of(TOKEN, token);
    }

    @Override
    public Account getCurrentAccountLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetail customUserDetail) {
            log.info("Get account from token.");
            return customUserDetail.getAccount();
        }
        return null;
    }
}
