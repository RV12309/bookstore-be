package com.hust.bookstore.dto;

import com.hust.bookstore.entity.Account;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@ToString
public class CustomUserDetail implements UserDetails {
    private final Account account;

    public CustomUserDetail(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(List.of(() -> "ROLE_" + account.getType().toString()));
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return account.getIsVerified();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return account.getIsEnabled();
    }

    public Account getAccount() {
        account.setCreatedAt(null);
        account.setUpdatedAt(null);
        account.setVerificationExpiredAt(null);
        account.setPassword(null);
        return account;
    }
}
