package com.example.LTHD.controller;

import com.example.LTHD.dto.request.AddToCartRequest;
import com.example.LTHD.dto.request.UpdateCartItemRequest;
import com.example.LTHD.dto.response.ApiResponse;
import com.example.LTHD.dto.response.CartResponse;
import com.example.LTHD.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Lấy giỏ hàng của người dùng
    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable Long userId) {
        return cartService.getCart(userId);
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/{userId}/items")
    public CartResponse addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request) {

        return cartService.addToCart(userId, request);
    }

    // Cập nhật số lượng sản phẩm
    @PutMapping("/{userId}/items/{itemId}")
    public CartResponse updateCartItem(
            @PathVariable Long userId,
            @PathVariable Integer itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        return cartService.updateCartItem(userId, itemId, request);
    }

    // Xóa một sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{userId}/items/{itemId}")
    public ApiResponse<Void> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Integer itemId) {

        cartService.removeCartItem(userId, itemId);
        return new ApiResponse<>(
                200,
                "Xóa vật phẩm thành công",
                null);
    }

    // Xóa toàn bộ giỏ hàng
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return new ApiResponse<>(
                200,
                "Xóa giỏ hàng thành công",
                null);
    }
}