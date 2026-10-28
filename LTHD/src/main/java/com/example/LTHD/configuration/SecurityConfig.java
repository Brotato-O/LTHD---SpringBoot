package com.example.LTHD.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))
                                .exceptionHandling(exception -> exception

                                                // Chưa đăng nhập: 401
                                                .authenticationEntryPoint((request, response, ex) -> {
                                                        response.setStatus(401);
                                                        response.setContentType("application/json;charset=UTF-8");

                                                        response.getWriter().write("""
                                                                        {
                                                                            "code": 401,
                                                                            "message": "Bạn chưa được xác thực",
                                                                            "data": null
                                                                        }
                                                                        """);
                                                })

                                                // Đã đăng nhập nhưng không đủ quyền: 403
                                                .accessDeniedHandler((request, response, ex) -> {
                                                        response.setStatus(403);
                                                        response.setContentType("application/json;charset=UTF-8");

                                                        response.getWriter()
                                                                        .write("""
                                                                                        {
                                                                                            "code": 403,
                                                                                            "message": "Người dùng không có quyền truy cập",
                                                                                            "data": null
                                                                                        }
                                                                                        """);
                                                }))

                                .authorizeHttpRequests(auth -> auth

                                                // Authentication
                                                .requestMatchers("/auth/login").permitAll()
                                                .requestMatchers("/auth/logout").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()

                                                // Product: cho phép xem sản phẩm
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/products/**")
                                                .permitAll()

                                                // Cart: yêu cầu đăng nhập
                                                .requestMatchers(
                                                                "/api/cart",
                                                                "/api/cart/**")
                                                .authenticated()

                                                // Admin quản lý Order
                                                .requestMatchers(
                                                                "/api/orders/admin/**")
                                                .hasRole("ADMIN")

                                                // Order của người dùng
                                                .requestMatchers(
                                                                "/api/orders",
                                                                "/api/orders/**")
                                                .authenticated()

                                                // Payment: chỉ Admin
                                                .requestMatchers(
                                                                "/api/payments/**")
                                                .hasRole("ADMIN")

                                                // User
                                                .requestMatchers("/api/v1/users", "/api/v1/users/**")
                                                .hasRole("ADMIN")

                                                // API còn lại
                                                .anyRequest().authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}