package com.hust.bookstore.repository;

import com.hust.bookstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findBySessionIdAndBookId(Long sessionId, Long bookId);

    List<CartItem> findAllBySessionId(Long id);

    @Modifying
    @Transactional
    void deleteBySessionId(Long sessionId);
}
