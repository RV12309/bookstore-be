package com.hust.bookstore.service.impl;

import com.hust.bookstore.dto.CartItemDto;
import com.hust.bookstore.dto.request.CartItemRequest;
import com.hust.bookstore.dto.request.CartRequest;
import com.hust.bookstore.dto.response.CartResponse;
import com.hust.bookstore.entity.Account;
import com.hust.bookstore.entity.Book;
import com.hust.bookstore.entity.CartItem;
import com.hust.bookstore.entity.ShoppingCart;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.NotificationService;
import com.hust.bookstore.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends BusinessHelper implements ShoppingCartService {


    public ShoppingCartServiceImpl(BookRepository bookRepository, CartRepository cartRepository,
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

    @Override
    public CartResponse getCart(Long sessionId, Long refId) {
        log.info("Start get cart");
        if (refId == null || refId == 0) {
            if (isNull(sessionId)|| sessionId == 0) {
                ShoppingCart shoppingSession = ShoppingCart.builder().total(BigDecimal.valueOf(0)).build();
                return modelMapper.map(cartRepository.save(shoppingSession), CartResponse.class);
            } else {
                ShoppingCart shoppingSession = cartRepository.findById(sessionId).orElse(null);
                if (shoppingSession == null) {
                    shoppingSession = ShoppingCart.builder()
                            .total(BigDecimal.valueOf(0))
                            .build();
                    return modelMapper.map(cartRepository.save(shoppingSession), CartResponse.class);
                }
                return getCartResponse(shoppingSession);
            }
        } else {
            ShoppingCart shoppingSession = cartRepository.findByUserId(refId).orElse(null);
            if (shoppingSession == null) {
                shoppingSession = ShoppingCart.builder()
                        .total(BigDecimal.valueOf(0))
                        .userId(refId)
                        .build();
                return modelMapper.map(cartRepository.save(shoppingSession), CartResponse.class);
            }
            return getCartResponse(shoppingSession);

        }
    }

    private CartResponse getCartResponse(ShoppingCart shoppingSession) {
        List<CartItem> cartItems = cartItemRepository.findAllBySessionId(shoppingSession.getId());
        if (CollectionUtils.isEmpty(cartItems)) {
            return modelMapper.map(shoppingSession, CartResponse.class);
        }
        CartResponse cartResponse = modelMapper.map(shoppingSession, CartResponse.class);
        List<CartItemDto> cartItemDtos = cartItems.stream().map(item -> modelMapper.map(item, CartItemDto.class)).toList();
        cartResponse.setItems(cartItemDtos);
        return cartResponse;
    }

    @Override
    public CartResponse addToCart(CartItemRequest request) {
        log.info("Start add to cart");
        Account currentAccount = authService.getCurrentAccountLogin();
        ShoppingCart cart = cartRepository.findById(request.getSessionId()).orElse(null);
        if (cart == null) {
            log.info("Cart not found, create new cart");
            cart = ShoppingCart.builder()
                    .total(BigDecimal.valueOf(0))
                    .userId(currentAccount == null ? null : currentAccount.getUserId())
                    .build();
        }
        Book book = checkExistBook(request.getBookId());
        if (book.getQuantity() == 0) {
            throw new BusinessException(ResponseCode.BOOK_OUT_OF_STOCK);
        }
        if (book.getQuantity() < request.getQuantity()) {
            throw new BusinessException(ResponseCode.BOOK_NOT_ENOUGH);
        }

        CartItem cartItem = getCartItem(cart.getId(), book.getId());
        if (isNull(cartItem)) {
            log.info("Cart item not existed, create new cart item");
            Account seller = accountRepository.findById(book.getAccountId()).orElse(null);
            cartItem = CartItem.builder()
                    .bookId(book.getId())
                    .sessionId(cart.getId())
                    .title(book.getTitle())
                    .price(book.getPrice())
                    .urlThumbnail(book.getUrlThumbnail())
                    .quantity(0L)
                    .sellerId(book.getAccountId())
                    .sellerName(seller == null ? null : seller.getUsername())
                    .build();
        }

        switch (request.getAction()) {
            case ADD:
                log.info("Add quantity to cart item or create new cart item");
                cartItem.setQuantity((nonNull(cartItem.getQuantity()) && cartItem.getQuantity() >= 0) ? cartItem.getQuantity() + request.getQuantity() : request.getQuantity());
                cartItem.setTotal(book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                cartItemRepository.save(cartItem);
                break;
            case UPDATE:
                log.info("Update quantity to cart item");
                cartItem.setQuantity(request.getQuantity());
                cartItemRepository.save(cartItem);
                break;
            case REMOVE:
                log.info("Remove cart item");
                cartItemRepository.delete(cartItem);
                break;
        }

        CartResponse cartResponse = updateCartItems(cart);
        log.info("End add to cart");
        return cartResponse;

    }

    private CartResponse updateCartItems(ShoppingCart cart) {
        List<CartItem> cartItems = cartItemRepository.findAllBySessionId(cart.getId());
        //calculate total
        BigDecimal total = BigDecimal.valueOf(0);
        for (CartItem item : cartItems) {
            Book book = checkExistBook(item.getBookId());
            total = total.add(book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        cart.setTotal(total);

        ShoppingCart cartSaved = cartRepository.save(cart);
        CartResponse cartResponse = modelMapper.map(cartSaved, CartResponse.class);
        List<CartItemDto> cartItemDtos = cartItems.stream().map(item -> modelMapper.map(item, CartItemDto.class)).toList();
        cartResponse.setItems(cartItemDtos);
        return cartResponse;
    }

    @Override
    public CartResponse deleteCartItem(CartRequest request) {
        log.info("Start delete cart item");
        ShoppingCart cart = cartRepository.findById(request.getSessionId()).orElse(null);
        if (cart == null) {
            log.info("Cart not found, create new cart");
            cart = ShoppingCart.builder()
                    .total(BigDecimal.valueOf(0))
                    .build();
        } else {
            cartItemRepository.deleteBySessionId(request.getSessionId());
        }

        CartResponse cartResponse = updateCartItems(cart);
        log.info("End delete cart item");
        return cartResponse;
    }
}
