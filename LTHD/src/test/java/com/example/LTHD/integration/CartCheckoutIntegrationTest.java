package com.example.LTHD.integration;

import com.example.LTHD.common.Role;
import com.example.LTHD.dto.request.AddToCartRequest;
import com.example.LTHD.dto.request.CheckoutRequest;
import com.example.LTHD.entity.Cart;
import com.example.LTHD.entity.Product;
import com.example.LTHD.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

class CartCheckoutIntegrationTest extends IntegrationTestSupport {

    @Test
    void addToCart_shouldPersistItemAndReturnCart() throws Exception {
        User user = saveUser("cart-user", "cart@example.com", Role.CUSTOMER, CUSTOMER_PASSWORD);
        Product product = productRepository.saveAndFlush(Product.builder()
                .name("Mouse")
                .price(new BigDecimal("25.50"))
                .stock(10)
                .build());
        String token = login(user, CUSTOMER_PASSWORD);
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(product.getId());
        request.setQuantity(2);

        mockMvc.perform(post("/api/cart/{userId}/items", user.getId())
                        .with(csrf())
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.totalQuantity").value(2));

        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();
        var cartItem = cartItemsRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow();
        assertThat(cartItem.getQuantity()).isEqualTo(2);
        assertThat(cartItem.getPrice()).isEqualByComparingTo("25.50");
    }

    @Test
    void checkout_shouldPersistOrderPaymentAndClearCart() throws Exception {
        User user = saveUser("checkout-user", "checkout@example.com", Role.CUSTOMER, CUSTOMER_PASSWORD);
        Product product = productRepository.saveAndFlush(Product.builder()
                .name("Headset")
                .price(new BigDecimal("40.00"))
                .stock(10)
                .build());
        String token = login(user, CUSTOMER_PASSWORD);

        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(product.getId());
        addRequest.setQuantity(2);
        mockMvc.perform(post("/api/cart/{userId}/items", user.getId())
                .with(csrf())
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setShippingAddress("123 Main Street");
        checkoutRequest.setPaymentMethod("COD");

        mockMvc.perform(post("/api/orders/{userId}/checkout", user.getId())
                        .with(csrf())
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.paymentMethod").value("COD"))
                .andExpect(jsonPath("$.totalAmount").value(80.00))
                .andExpect(jsonPath("$.payment.status").value("UNPAID"));

        assertThat(orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()))
                .hasSize(1);
        assertThat(orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .get(0).getPayment().getStatus()).isEqualTo("UNPAID");
        assertThat(cartItemsRepository.findAll()).isEmpty();
    }
}
