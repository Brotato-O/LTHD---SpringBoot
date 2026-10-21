package com.example.LTHD.service.impl;

import com.example.LTHD.dto.request.AddToCartRequest;
import com.example.LTHD.entity.Cart;
import com.example.LTHD.entity.CartItems;
import com.example.LTHD.entity.Product;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.CartMapper;
import com.example.LTHD.repository.CartItemsRepository;
import com.example.LTHD.repository.CartRepository;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemsRepository cartItemsRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartMapper cartMapper;
    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void addToCart_ItemAlreadyExists_ShouldIncreaseQuantity() {
        Long userId = 1L;
        Integer productId = 100;
        Integer addQuantity = 2;

        // 1. Giả lập Giỏ hàng và User đã tồn tại
        User user = new User();
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // 2. Giả lập Sản phẩm
        Product product = new Product();
        product.setId(productId);
        product.setPrice(java.math.BigDecimal.valueOf(100));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // 3. Giả lập Sản phẩm ĐÃ CÓ trong giỏ với số lượng ban đầu là 3
        CartItems existingItem = new CartItems();
        existingItem.setQuantity(3);
        when(cartItemsRepository.findByCartIdAndProductId(cart.getId(), productId))
                .thenReturn(Optional.of(existingItem));

        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(addQuantity);

        // 4. Chạy hàm thực tế
        cartService.addToCart(userId, request);

        // 5. Kiểm tra: Số lượng mới phải là 3 + 2 = 5
        assertEquals(5, existingItem.getQuantity());
        verify(cartItemsRepository, times(1)).save(existingItem);
    }
}