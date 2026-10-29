package com.example.LTHD.integration;

import com.example.LTHD.common.Role;
import com.example.LTHD.entity.Order;
import com.example.LTHD.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderIntegrationTest extends IntegrationTestSupport {

    @Test
    void getOrderById_shouldReturnOrderForItsOwner() throws Exception {
        User user = saveUser("order-owner", "order-owner@example.com",
                Role.CUSTOMER, CUSTOMER_PASSWORD);
        Order order = orderRepository.saveAndFlush(Order.builder()
                .user(user)
                .totalAmount(new BigDecimal("75.00"))
                .shippingAddress("123 Main Street")
                .status("PENDING")
                .paymentMethod("COD")
                .build());
        String token = login(user, CUSTOMER_PASSWORD);

        mockMvc.perform(get("/api/orders/{userId}/{orderId}", user.getId(), order.getId())
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrderById_shouldNotExposeAnotherUsersOrder() throws Exception {
        User owner = saveUser("order-real-owner", "order-real-owner@example.com",
                Role.CUSTOMER, CUSTOMER_PASSWORD);
        User otherUser = saveUser("order-other-user", "order-other-user@example.com",
                Role.CUSTOMER, CUSTOMER_PASSWORD);
        Order order = orderRepository.saveAndFlush(Order.builder()
                .user(owner)
                .totalAmount(new BigDecimal("30.00"))
                .shippingAddress("456 Main Street")
                .status("PENDING")
                .paymentMethod("COD")
                .build());
        String token = login(otherUser, CUSTOMER_PASSWORD);

        mockMvc.perform(get("/api/orders/{userId}/{orderId}", otherUser.getId(), order.getId())
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy đơn hàng"));
    }

    @Test
    void cancelOrder_shouldPersistCancelledStatusForOwner() throws Exception {
        User user = saveUser("cancel-owner", "cancel-owner@example.com",
                Role.CUSTOMER, CUSTOMER_PASSWORD);
        Order order = orderRepository.saveAndFlush(Order.builder()
                .user(user)
                .totalAmount(new BigDecimal("45.00"))
                .shippingAddress("789 Main Street")
                .status("PENDING")
                .paymentMethod("COD")
                .build());
        String token = login(user, CUSTOMER_PASSWORD);

        mockMvc.perform(put("/api/orders/{userId}/{orderId}/cancel", user.getId(), order.getId())
                        .with(csrf())
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        assertThat(orderRepository.findById(order.getId()).orElseThrow().getStatus())
                .isEqualTo("CANCELLED");
    }
}
