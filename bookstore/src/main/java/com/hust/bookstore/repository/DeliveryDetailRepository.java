package com.hust.bookstore.repository;

import com.hust.bookstore.entity.DeliveryDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryDetailRepository extends JpaRepository<DeliveryDetails, Long> {
    Optional<DeliveryDetails> findByOrderId(Long id);
}
