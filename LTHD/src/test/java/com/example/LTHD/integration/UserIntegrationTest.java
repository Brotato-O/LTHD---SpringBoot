package com.example.LTHD.integration;

import com.example.LTHD.common.Role;
import com.example.LTHD.dto.request.UserCreateRequest;
import com.example.LTHD.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserIntegrationTest extends IntegrationTestSupport {

    @Test
    void createUser_shouldPersistUserForAdmin() throws Exception {
        User admin = saveUser("user-admin", "user-admin@example.com",
                Role.ADMIN, ADMIN_PASSWORD);
        String token = login(admin, ADMIN_PASSWORD);
        UserCreateRequest request = new UserCreateRequest(
                "created-user",
                "password123",
                "created-user@example.com",
                "Created User",
                Role.CUSTOMER);

        String response = mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("created-user"))
                .andExpect(jsonPath("$.data.email").value("created-user@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long createdId = objectMapper.readTree(response).path("data").path("id").asLong();
        User persisted = userRepository.findById(createdId).orElseThrow();
        assertThat(persisted.getUsername()).isEqualTo("created-user");
        assertThat(persisted.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(persisted.getPassword()).isNotEqualTo("password123");
    }

    @Test
    void userManagement_shouldRejectCustomer() throws Exception {
        User customer = saveUser("regular-user", "regular-user@example.com",
                Role.CUSTOMER, CUSTOMER_PASSWORD);
        String token = login(customer, CUSTOMER_PASSWORD);

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserCreateRequest(
                                "blocked-user",
                                "password123",
                                "blocked-user@example.com",
                                "Blocked User",
                                Role.CUSTOMER))))
                .andExpect(status().isForbidden());
    }
}
