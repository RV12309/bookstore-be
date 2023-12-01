package com.hust.bookstore.repository;

import com.hust.bookstore.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserIdAndIsDeletedFalse(Long userId);

    @Modifying
    @Query("UPDATE UserAddress u SET u.isDefault = false WHERE u.userId = ?1")
    void updateDefaultAddress(Long id);
}
