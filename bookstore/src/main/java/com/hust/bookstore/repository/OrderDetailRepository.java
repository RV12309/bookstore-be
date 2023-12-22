package com.hust.bookstore.repository;

import com.hust.bookstore.entity.OrderDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long> {
    Page<OrderDetails> findAllByUserId(Long id, Pageable pageable);

    Page<OrderDetails> findAllBySellerId(Long id, Pageable pageable);
}
