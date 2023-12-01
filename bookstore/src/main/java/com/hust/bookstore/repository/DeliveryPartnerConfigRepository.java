package com.hust.bookstore.repository;

import com.hust.bookstore.entity.DeliveryPartnersConfig;
import com.hust.bookstore.entity.StoreDeliveryPartners;
import com.hust.bookstore.enumration.DeliveryProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryPartnerConfigRepository extends JpaRepository<DeliveryPartnersConfig, Long> {
    Optional<DeliveryPartnersConfig> findByProvider(DeliveryProvider provider);
}
