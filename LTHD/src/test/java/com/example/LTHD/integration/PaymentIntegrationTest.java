package com.example.LTHD.integration;

import com.example.LTHD.common.Role;
import com.example.LTHD.entity.Order;
import com.example.LTHD.entity.Payment;
import com.example.LTHD.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

class PaymentIntegrationTest extends IntegrationTestSupport {

    @Test
    void confirmCodPayment_shouldPersistPaidStatusForAdmin() throws Exception {
        User admin = saveUser("payment-admin", "payment-admin@example.com", Role.ADMIN, ADMIN_PASSWORD);
        Order order = orderRepository.saveAndFlush(Order.builder()
                .user(admin)
                .totalAmount(new BigDecimal("50.00"))
                .shippingAddress("123 Main Street")
                .status("COMPLETED")
                .paymentMethod("COD")
                .build());
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod("COD");
        payment.setAmount(new BigDecimal("50.00"));
        payment.setStatus("UNPAID");
        paymentRepository.saveAndFlush(payment);
        String token = login(admin, ADMIN_PASSWORD);

        mockMvc.perform(put("/api/payments/order/{orderId}/confirm-cod", order.getId())
                        .with(csrf())
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paidAt").isNotEmpty());

        assertThat(paymentRepository.findById(payment.getId()).orElseThrow().getStatus())
                .isEqualTo("PAID");
    }

    @Test
    void paymentEndpoint_shouldRejectCustomer() throws Exception {
        User customer = saveUser("payment-customer", "payment-customer@example.com",
                Role.CUSTOMER, CUSTOMER_PASSWORD);
        String token = login(customer, CUSTOMER_PASSWORD);

        mockMvc.perform(put("/api/payments/order/999/confirm-cod")
                        .with(csrf())
                        .header("Authorization", bearer(token)))
                .andExpect(status().isForbidden());
    }
}
