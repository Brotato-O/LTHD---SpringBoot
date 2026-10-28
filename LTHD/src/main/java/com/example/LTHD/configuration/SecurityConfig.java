// package com.example.LTHD.configuration;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// @EnableMethodSecurity
// public class SecurityConfig {

//     private final JwtAuthenticationFilter jwtAuthenticationFilter;

//     public SecurityConfig(
//             JwtAuthenticationFilter jwtAuthenticationFilter) {
//         this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(
//             HttpSecurity http) throws Exception {

//         http
//                 .csrf(csrf -> csrf.disable())

//                 .sessionManagement(session -> session.sessionCreationPolicy(
//                         SessionCreationPolicy.STATELESS))

//                 .authorizeHttpRequests(auth -> auth

//                         // Authentication
//                         .requestMatchers("/auth/login").permitAll()
//                         .requestMatchers("/auth/logout").authenticated()

//                         // Product: cho phép xem sản phẩm
//                         .requestMatchers(
//                                 HttpMethod.GET,
//                                 "/api/products/**")
//                         .permitAll()

//                         // Cart: yêu cầu đăng nhập
//                         .requestMatchers(
//                                 "/api/cart",
//                                 "/api/cart/**")
//                         .authenticated()

//                         // Admin quản lý Order
//                         .requestMatchers(
//                                 "/api/orders/admin/**")
//                         .hasRole("ADMIN")

//                         // Order của người dùng
//                         .requestMatchers(
//                                 "/api/orders",
//                                 "/api/orders/**")
//                         .authenticated()

//                         // Payment: chỉ Admin
//                         .requestMatchers(
//                                 "/api/payments/**")
//                         .hasRole("ADMIN")

//                         // User
//                         .requestMatchers(
//                                 "/api/v1/users/**")
//                         .hasRole("ADMIN")

//                         // API còn lại
//                         .anyRequest().authenticated())

//                 .addFilterBefore(
//                         jwtAuthenticationFilter,
//                         UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }
// }

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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

        // Cấu hình cho phép React gọi API Backend
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                Arrays.asList("http://localhost:5173"));

                configuration.setAllowedMethods(
                                Arrays.asList(
                                                "GET", "POST", "PUT", "DELETE", "OPTIONS"));

                configuration.setAllowedHeaders(
                                Arrays.asList("*"));

                // Cho phép gửi header Authorization chứa JWT
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                // Bật CORS trong Spring Security
                                .cors(cors -> {
                                })

                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                // Cho phép request kiểm tra CORS
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // Authentication
                                                .requestMatchers("/auth/login").permitAll()
                                                .requestMatchers("/auth/logout").authenticated()

                                                // Tạm thời mở toàn bộ API sản phẩm để test CRUD
                                                .requestMatchers("/api/products/**").permitAll()

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
                                                .requestMatchers(
                                                                "/api/v1/users/**")
                                                .hasRole("ADMIN")

                                                // API còn lại
                                                .anyRequest().authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
