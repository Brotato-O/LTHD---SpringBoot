package com.example.LTHD.service;

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
import com.example.LTHD.repository.CartItemsRepository;
import com.example.LTHD.repository.CartRepository;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.repository.UserRepository;
import com.example.LTHD.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void getCart_shouldCreateCartWhenUserHasNoCart() {
        User user = new User();
        Cart createdCart = Cart.builder().id(1L).user(user).build();
        CartResponse response = new CartResponse();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(createdCart);
        when(cartMapper.toCartResponse(createdCart)).thenReturn(response);

        CartResponse result = cartService.getCart(1L);

        assertThat(result).isSameAs(response);
        verify(cartRepository).save(any(Cart.class));
        verify(cartMapper).toCartResponse(createdCart);
    }

    @Test
    void getCart_shouldRejectMissingUserWhenCartDoesNotExist() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCart(1L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addToCart_shouldAddNewItemWithCurrentProductPrice() {
        User user = new User();
        Cart cart = Cart.builder().id(1L).user(user).build();
        Product product = Product.builder()
                .id(10)
                .price(new BigDecimal("25.50"))
                .build();
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(10);
        request.setQuantity(2);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10)).thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartIdAndProductId(1L, 10)).thenReturn(Optional.empty());
        when(cartMapper.toCartResponse(cart)).thenReturn(new CartResponse());

        cartService.addToCart(1L, request);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getItems().get(0).getPrice()).isEqualByComparingTo("25.50");
        assertThat(cart.getItems().get(0).getCart()).isSameAs(cart);
        verify(cartItemsRepository).save(any(CartItems.class));
    }

    @Test
    void addToCart_shouldIncreaseQuantityForExistingItem() {
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        Product product = Product.builder().id(10).price(new BigDecimal("25.50")).build();
        CartItems existingItem = CartItems.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .price(product.getPrice())
                .build();
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(10);
        request.setQuantity(3);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10)).thenReturn(Optional.of(product));
        when(cartItemsRepository.findByCartIdAndProductId(1L, 10))
                .thenReturn(Optional.of(existingItem));
        when(cartMapper.toCartResponse(cart)).thenReturn(new CartResponse());

        cartService.addToCart(1L, request);

        assertThat(existingItem.getQuantity()).isEqualTo(5);
        verify(cartItemsRepository).save(existingItem);
    }

    @Test
    void addToCart_shouldRejectMissingProduct() {
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(99);
        request.setQuantity(1);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(1L, request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

        verifyNoInteractions(cartItemsRepository, cartMapper);
    }

    @Test
    void updateCartItem_shouldUpdateQuantityForItemInUserCart() {
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        CartItems item = CartItems.builder()
                .id(5L)
                .cart(cart)
                .quantity(1)
                .build();
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(4);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findById(5)).thenReturn(Optional.of(item));
        when(cartMapper.toCartResponse(cart)).thenReturn(new CartResponse());

        cartService.updateCartItem(1L, 5, request);

        assertThat(item.getQuantity()).isEqualTo(4);
        verify(cartItemsRepository).save(item);
    }

    @Test
    void updateCartItem_shouldRejectMissingItem() {
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(4);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateCartItem(1L, 5, request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CART_ITEM_NOT_FOUND);

        verify(cartItemsRepository, never()).save(any(CartItems.class));
    }

    @Test
    void removeCartItem_shouldRemoveItemFromUserCart() {
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        CartItems item = CartItems.builder().id(5L).cart(cart).build();
        cart.getItems().add(item);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findById(5)).thenReturn(Optional.of(item));

        cartService.removeCartItem(1L, 5);

        assertThat(cart.getItems()).doesNotContain(item);
        assertThat(item.getCart()).isNull();
    }

    @Test
    void clearCart_shouldRemoveAllItems() {
        Cart cart = Cart.builder().id(1L).user(new User()).build();
        cart.addItem(CartItems.builder().quantity(1).build());
        cart.addItem(CartItems.builder().quantity(2).build());
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.clearCart(1L);

        assertThat(cart.getItems()).isEmpty();
    }
}
