package com.example.LTHD.service.impl;

import com.example.LTHD.dto.request.AddToCartRequest;
import com.example.LTHD.dto.request.UpdateCartItemRequest;
import com.example.LTHD.dto.response.CartResponse;

import com.example.LTHD.entity.Cart;
import com.example.LTHD.entity.CartItems;
import com.example.LTHD.entity.Product;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.CartMapper;

import com.example.LTHD.repository.CartRepository;
import com.example.LTHD.repository.CartItemsRepository;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.repository.UserRepository;

import com.example.LTHD.service.CartService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    // Lấy hoặc tạo giỏ hàng của người dùng
    private Cart getOrCreateCart(Long userId) {

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {

                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                    Cart cart = new Cart();
                    cart.setUser(user);

                    return cartRepository.save(cart);
                });
    }

    // Xem giỏ hàng
    @Override
    @Transactional
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);

        return cartMapper.toCartResponse(cart);
    }

    // Thêm sản phẩm vào giỏ
    @Override
    @Transactional
    public CartResponse addToCart(
            Long userId,
            AddToCartRequest request) {

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        CartItems cartItem = cartItemsRepository
                .findByCartIdAndProductId(
                        cart.getId(),
                        product.getId())
                .orElse(null);

        if (cartItem != null) {

            // Sản phẩm đã tồn tại trong giỏ
            cartItem.setQuantity(
                    Math.addExact(
                            cartItem.getQuantity(),
                            request.getQuantity()));

            cartItemsRepository.save(cartItem);

        } else {

            // Sản phẩm chưa có trong giỏ
            CartItems newItem = new CartItems();

            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setPrice(product.getPrice());

            cart.addItem(newItem);

            cartItemsRepository.save(newItem);
        }

        return cartMapper.toCartResponse(cart);
    }

    // Cập nhật số lượng sản phẩm
    @Override
    @Transactional
    public CartResponse updateCartItem(
            Long userId,
            Integer itemId,
            UpdateCartItemRequest request) {

        Cart cart = getOrCreateCart(userId);

        CartItems item = cartItemsRepository
                .findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        // Kiểm tra sản phẩm thuộc giỏ của người dùng
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa sản phẩm này");
        }

        item.setQuantity(request.getQuantity());

        cartItemsRepository.save(item);

        return cartMapper.toCartResponse(cart);
    }

    // Xóa một sản phẩm khỏi giỏ
    @Override
    @Transactional
    public void removeCartItem(Long userId, Integer itemId) {

        Cart cart = getOrCreateCart(userId);

        CartItems item = cartItemsRepository
                .findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa sản phẩm này");
        }

        cart.removeItem(item);
    }

    // Xóa toàn bộ giỏ hàng
    @Override
    @Transactional
    public void clearCart(Long userId) {

        Cart cart = getOrCreateCart(userId);

        cart.getItems().clear();
    }
}