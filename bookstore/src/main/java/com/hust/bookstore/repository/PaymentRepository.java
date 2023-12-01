package com.hust.bookstore.repository;

import com.hust.bookstore.entity.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentDetails, Long> {
    Optional<PaymentDetails> findByOrderId(Long orderId);
}
