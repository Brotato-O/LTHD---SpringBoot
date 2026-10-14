package com.example.LTHD.service;

import com.example.LTHD.dto.request.AddToCartRequest;
import com.example.LTHD.dto.request.UpdateCartItemRequest;
import com.example.LTHD.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addToCart(Long userId, AddToCartRequest request);

    CartResponse updateCartItem(
            Long userId,
            Integer itemId,
            UpdateCartItemRequest request);

    void removeCartItem(Long userId, Integer itemId);

    void clearCart(Long userId);
}