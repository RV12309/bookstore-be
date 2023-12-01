package com.hust.bookstore.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hust.bookstore.entity.*;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BusinessHelper {
    protected final BookRepository bookRepository;
    protected final CartRepository cartRepository;
    protected final CartItemRepository cartItemRepository;
    protected final PaymentRepository paymentRepository;
    protected final DeliveryPartnerConfigRepository deliveryPartnerConfigRepo;
    protected final StoreDeliveryPartnerRepository storeDeliveryPartnerRepo;

    protected final UserRepository userRepository;
    protected final OrderDetailRepository orderDetailsRepository;
    protected final OrderItemsRepository orderItemsRepository;

    protected final CategoryRepository categoryRepository;
    protected final BookCategoryRepository bookCategoryRepository;

    protected final AccountRepository accountRepository;

    protected final AuthService authService;

    protected final BookImageRepository bookImageRepository;
    protected final ModelMapper modelMapper;
    protected final NotificationService notificationService;
    protected final UserAddressRepository addressRepository;


    public BusinessHelper(BookRepository bookRepository, CartRepository cartRepository, CartItemRepository cartItemRepository,
                          PaymentRepository paymentRepository, DeliveryPartnerConfigRepository deliveryPartnerConfigRepo,
                          StoreDeliveryPartnerRepository storeDeliveryPartnerRepo, UserRepository userRepository,
                          OrderDetailRepository orderDetailsRepository, OrderItemsRepository orderItemsRepository,
                          CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository,
                          AccountRepository accountRepository, AuthService authService, BookImageRepository bookImageRepository,
                          ModelMapper modelMapper, NotificationService notificationService, UserAddressRepository addressRepository) {
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentRepository = paymentRepository;
        this.deliveryPartnerConfigRepo = deliveryPartnerConfigRepo;
        this.storeDeliveryPartnerRepo = storeDeliveryPartnerRepo;
        this.userRepository = userRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.accountRepository = accountRepository;
        this.authService = authService;
        this.bookImageRepository = bookImageRepository;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
        this.addressRepository = addressRepository;
    }

    protected Book checkExistBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null || Boolean.TRUE.equals(book.getIsDeleted())) {
            throw new BusinessException(ResponseCode.BOOK_NOT_FOUND);
        }
        return book;
    }

    protected ShoppingCart checkExistCart(Long sessionId) {
        ShoppingCart shoppingSession = cartRepository.findById(sessionId).orElse(null);
        if (shoppingSession == null) {
            throw new BusinessException(ResponseCode.CART_NOT_FOUND);
        }
        return shoppingSession;
    }

    protected CartItem checkExistCartItem(Long sessionId, Long bookId) {
        CartItem cartItem = cartItemRepository.findBySessionIdAndBookId(sessionId, bookId).orElse(null);
        if (cartItem == null) {
            throw new BusinessException(ResponseCode.CART_ITEM_NOT_FOUND);
        }
        return cartItem;
    }

    protected CartItem getCartItem(Long sessionId, Long bookId) {
        return cartItemRepository.findBySessionIdAndBookId(sessionId, bookId).orElse(null);
    }

    protected static <T> TypeReference<T> getNewTypeReference() {
        return new TypeReference<T>() {
        };
    }

    protected User checkExistUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        return user;
    }

    protected UserAddress checkExistUserAddress(Long addressId) {
        UserAddress address = addressRepository.findById(addressId).orElse(null);
        if (address == null) {
            throw new BusinessException(ResponseCode.USER_ADDRESS_NOT_FOUND);
        }
        return address;
    }

    protected OrderDetails checkExistOrder(Long orderId) {
        OrderDetails order = orderDetailsRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new BusinessException(ResponseCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    protected boolean non(boolean condition) {
        return !condition;
    }
}
