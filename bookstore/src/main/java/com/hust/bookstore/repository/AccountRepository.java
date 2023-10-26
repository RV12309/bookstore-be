package com.hust.bookstore.repository;

import com.hust.bookstore.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);
}
