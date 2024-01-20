package com.hust.bookstore.repository;

import com.hust.bookstore.entity.StoreDeliveryPartners;
import com.hust.bookstore.enumration.DeliveryProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreDeliveryPartnerRepository extends JpaRepository<StoreDeliveryPartners, Long> {
    Optional<StoreDeliveryPartners> findByAccountId(Long shopAccountId);

    Optional<StoreDeliveryPartners> findByAccountIdAndIsDefaultTrue(Long shopAccountId);

    Optional<StoreDeliveryPartners> findByProviderAndAccountId(DeliveryProvider deliveryProvider, Long id);
}
