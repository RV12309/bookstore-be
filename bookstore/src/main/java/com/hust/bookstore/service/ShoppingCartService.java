package com.hust.bookstore.service;

import com.hust.bookstore.dto.request.CartItemRequest;
import com.hust.bookstore.dto.request.CartRequest;
import com.hust.bookstore.dto.response.CartResponse;

public interface ShoppingCartService {

    CartResponse getCart(Long sessionId, Long refId);

    CartResponse addToCart(CartItemRequest request);

    CartResponse deleteCartItem(CartRequest request);

}
