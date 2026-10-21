package com.example.LTHD.service.impl;

import com.example.LTHD.dto.response.OrderResponse;
import com.example.LTHD.entity.*;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.OrderMapper;
import com.example.LTHD.repository.CartRepository;
import com.example.LTHD.repository.OrderRepository;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void checkout_CartEmpty_ShouldThrowException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Cart emptyCart = new Cart();
        emptyCart.setItems(new ArrayList<>()); // Giỏ rỗng
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));

        AppException exception = assertThrows(AppException.class,
                () -> orderService.checkout(userId));

        assertEquals(ErrorCode.CART_EMPTY, exception.getErrorCode());
    }

    @Test
    void checkout_InsufficientStock_ShouldThrowException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        // Giả lập giỏ có 1 món hàng
        Cart cart = new Cart();
        CartItems cartItem = new CartItems();
        cartItem.setQuantity(5); // Muốn mua 5
        Product product = new Product();
        product.setId(100);
        cartItem.setProduct(product);
        cart.setItems(List.of(cartItem));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Giả lập kho chỉ còn 3
        Product dbProduct = new Product();
        dbProduct.setId(100);
        dbProduct.setStock(3);
        when(productRepository.findByIdForUpdate(100)).thenReturn(Optional.of(dbProduct));

        AppException exception = assertThrows(AppException.class,
                () -> orderService.checkout(userId));

        // Kỳ vọng lỗi không đủ hàng
        assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    }
}