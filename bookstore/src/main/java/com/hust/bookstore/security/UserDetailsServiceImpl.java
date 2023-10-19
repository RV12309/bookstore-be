package com.hust.bookstore.security;

import com.hust.bookstore.entity.Account;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Load user by username: {}", username);

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        log.info("User found with id: {}", account.getId());

        if (Boolean.FALSE.equals(account.getIsVerified())) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_VERIFY);
        }
        log.info("User is verified");

        if (Boolean.FALSE.equals(account.getIsEnabled())) {
            throw new BusinessException(ResponseCode.ACCOUNT_LOCKED);
        }
        log.info("User is enabled");

        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles(account.getType().toString())
                .build();
    }
}
