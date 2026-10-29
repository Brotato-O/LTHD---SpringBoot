package com.example.LTHD.integration;

import com.example.LTHD.common.Role;
import com.example.LTHD.dto.request.AuthenticationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

class AuthenticationIntegrationTest extends IntegrationTestSupport {

    @Test
    void login_shouldReturnJwtForValidCredentials() throws Exception {
        var user = saveUser(
                "login-user",
                "login@example.com",
                Role.CUSTOMER,
                CUSTOMER_PASSWORD);
        AuthenticationRequest request = new AuthenticationRequest(
                user.getEmail(),
                CUSTOMER_PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.authenticated").value(true));
    }

    @Test
    void login_shouldRejectWrongPassword() throws Exception {
        var user = saveUser(
                "wrong-password-user",
                "wrong-password@example.com",
                Role.CUSTOMER,
                CUSTOMER_PASSWORD);
        AuthenticationRequest request = new AuthenticationRequest(
                user.getEmail(),
                "wrong-password");

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedCartEndpoint_shouldRejectMissingToken() throws Exception {
        mockMvc.perform(get("/api/cart/1"))
                .andExpect(status().isForbidden());
    }
}
