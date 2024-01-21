package com.hust.bookstore.repository;

import com.hust.bookstore.entity.Account;
import com.hust.bookstore.enumration.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);

    @Query("""
            select a
            from Account a
            where a.userId in :ids
            """)
    List<Account> findAllByUserIdIn(List<Long> ids);

    long countByType(UserType userType);
}
