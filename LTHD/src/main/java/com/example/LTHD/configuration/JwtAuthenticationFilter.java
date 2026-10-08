package com.example.LTHD.configuration;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.LTHD.service.TokenBlacklistService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.signer-key}")
    private String signerKey;

    public JwtAuthenticationFilter(
            TokenBlacklistService tokenBlacklistService) {

        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        // Không có Authorization
        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Lấy JWT
            String token = authorization.substring(7);

            SignedJWT signedJWT = SignedJWT.parse(token);

            // Kiểm tra thuật toán
            if (!JWSAlgorithm.HS512.equals(
                    signedJWT.getHeader().getAlgorithm())) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Kiểm tra chữ ký
            boolean validSignature = signedJWT.verify(
                    new MACVerifier(signerKey.getBytes()));

            if (!validSignature) {
                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ==============================
            // Lấy thông tin từ JWT
            // ==============================

            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

            String username = signedJWT.getJWTClaimsSet().getSubject();

            String role = signedJWT.getJWTClaimsSet()
                    .getStringClaim("role");

            Date expirationTime = signedJWT.getJWTClaimsSet()
                    .getExpirationTime();

            // ==============================
            // Kiểm tra expiration
            // ==============================

            if (expirationTime == null ||
                    expirationTime.before(new Date())) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ==============================
            // Kiểm tra token đã logout chưa
            // ==============================

            if (jwtId != null &&
                    tokenBlacklistService.isRevoked(jwtId)) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ==============================
            // Kiểm tra username
            // ==============================

            if (username == null ||
                    username.isBlank()) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ==============================
            // Kiểm tra role
            // ==============================

            if (role == null ||
                    (!role.equals("ADMIN") &&
                            !role.equals("CUSTOMER"))) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ==============================
            // Chuyển role JWT thành quyền
            // Spring Security
            // ==============================

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role));

            // ==============================
            // Tạo Authentication
            // ==============================

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities);

            // Đưa Authentication vào SecurityContext
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        filterChain.doFilter(request, response);
    }
}