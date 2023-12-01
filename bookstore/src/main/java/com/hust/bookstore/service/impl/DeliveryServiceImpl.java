package com.hust.bookstore.service.impl;

import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.DeliveryService;
import com.hust.bookstore.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeliveryServiceImpl extends BusinessHelper implements DeliveryService {


    public DeliveryServiceImpl(BookRepository bookRepository, CartRepository cartRepository,
                               CartItemRepository cartItemRepository, PaymentRepository paymentRepository,
                               DeliveryPartnerConfigRepository deliveryPartnerConfigRepo,
                               StoreDeliveryPartnerRepository storeDeliveryPartnerRepo, UserRepository userRepository,
                               OrderDetailRepository orderDetailsRepository, OrderItemsRepository orderItemsRepository,
                               CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository,
                               AccountRepository accountRepository, AuthService authService,
                               BookImageRepository bookImageRepository, ModelMapper modelMapper,
                               NotificationService notificationService, UserAddressRepository addressRepository) {
        super(bookRepository, cartRepository, cartItemRepository, paymentRepository,
                deliveryPartnerConfigRepo, storeDeliveryPartnerRepo, userRepository,
                orderDetailsRepository, orderItemsRepository, categoryRepository,
                bookCategoryRepository, accountRepository, authService, bookImageRepository,
                modelMapper, notificationService, addressRepository);
    }
}
