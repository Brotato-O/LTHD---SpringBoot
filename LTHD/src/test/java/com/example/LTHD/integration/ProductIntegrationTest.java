package com.example.LTHD.integration;

import com.example.LTHD.dto.request.ProductCreateRequest;
import com.example.LTHD.entity.Product;
import com.example.LTHD.entity.User;
import com.example.LTHD.common.Role;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

class ProductIntegrationTest extends IntegrationTestSupport {

    @Test
    void getProductById_shouldReturnPersistedProduct() throws Exception {
        Product product = productRepository.saveAndFlush(Product.builder()
                .name("Laptop")
                .price(new BigDecimal("1200.00"))
                .stock(10)
                .build());

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1200.00))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void getProductById_shouldReturnNotFoundForUnknownProduct() throws Exception {
        mockMvc.perform(get("/api/products/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy sản phẩm"));
    }

    @Test
    void createProduct_shouldPersistProductThroughApi() throws Exception {
        User admin = saveUser(
                "product-admin",
                "product-admin@example.com",
                Role.ADMIN,
                ADMIN_PASSWORD);
        String token = login(admin, ADMIN_PASSWORD);

        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Keyboard");
        request.setDescription("Mechanical keyboard");
        request.setPrice(new BigDecimal("75.00"));
        request.setStock(20);

        String response = mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = objectMapper.readTree(response).path("id").asInt();
        Product persisted = productRepository.findById(id).orElseThrow();
        assertThat(persisted.getPrice()).isEqualByComparingTo("75.00");
        assertThat(persisted.getStock()).isEqualTo(20);
    }
}
