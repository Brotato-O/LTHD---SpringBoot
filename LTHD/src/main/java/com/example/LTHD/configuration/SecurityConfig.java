
package com.example.LTHD.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

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

                .authorizeHttpRequests(auth -> {
                    if (securityEnabled) {
                        auth
                                .requestMatchers("/api/v1/users/**").hasRole("CUSTOMER")
                                .requestMatchers("/auth/login").permitAll()
                                .requestMatchers("/auth/logout").authenticated()
                                .anyRequest().authenticated();
                    } else {
                        auth.anyRequest().permitAll();
                    }
                });

        if (securityEnabled) {
            http.addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);
        }

//                .authorizeHttpRequests(auth ->
//                        auth.anyRequest().permitAll()
//                );

        return http.build();
    }
}