package com.example.LTHD.integration;

import com.example.LTHD.common.Role;
import com.example.LTHD.dto.request.AuthenticationRequest;
import com.example.LTHD.entity.User;
import com.example.LTHD.repository.CartItemsRepository;
import com.example.LTHD.repository.CartRepository;
import com.example.LTHD.repository.OrderItemsRepository;
import com.example.LTHD.repository.OrderRepository;
import com.example.LTHD.repository.PaymentRepository;
import com.example.LTHD.repository.ProductRepository;
import com.example.LTHD.repository.UserRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class IntegrationTestSupport {

    protected static final String CUSTOMER_PASSWORD = "password123";
    protected static final String ADMIN_PASSWORD = "admin123";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected CartRepository cartRepository;

    @Autowired
    protected CartItemsRepository cartItemsRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected OrderItemsRepository orderItemsRepository;

    @Autowired
    protected PaymentRepository paymentRepository;

    @AfterEach
    void cleanDatabase() {
        paymentRepository.deleteAll();
        orderItemsRepository.deleteAll();
        orderRepository.deleteAll();
        cartItemsRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected User saveUser(String username, String email, Role role, String password) {
        return userRepository.saveAndFlush(
                new User(
                        username,
                        passwordEncoder.encode(password),
                        email,
                        username,
                        role));
    }

    protected String login(User user, String password) throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
                user.getEmail(),
                password);

        String response = mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode data = objectMapper.readTree(response).path("data");
        return data.path("token").asText();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }
}
