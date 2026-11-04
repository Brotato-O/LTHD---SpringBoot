package com.example.LTHD.service.impl;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.beans.factory.annotation.Value;

import com.example.LTHD.dto.request.AuthenticationRequest;
import com.example.LTHD.dto.response.AuthenticateResponse;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.repository.UserRepository;
import com.example.LTHD.service.AuthenticationService;
import com.example.LTHD.service.TokenBlacklistService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.signer-key}")
    private String SIGNER_KEY;

    private final UserRepository userRepo;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationServiceImpl(
            UserRepository userRepo,
            TokenBlacklistService tokenBlacklistService) {

        this.userRepo = userRepo;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public AuthenticateResponse authenticate(AuthenticationRequest authenticationRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = userRepo.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean result = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!result)
            throw new AppException(ErrorCode.PASSWORD_WRONG);

        var token = generateToken(user);
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setToken(token);
        authenticateResponse.setAuthenticated(true);
        return authenticateResponse;
    }

    // tạo token
    private String generateToken(User user) {
        // tạo header dùng thuật toán HS512
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        // tạo payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("fullName", user.getFullName())
                .claim("role", user.getRole().name())
                .issuer("LTHD")
                .issueTime(new Date())
                .expirationTime(
                        new Date(Instant.now()
                                .plus(1, ChronoUnit.HOURS)
                                .toEpochMilli()))

                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void logout(String token) {

        try {

            // Kiểm tra Authorization
            if (token == null ||
                    !token.startsWith("Bearer ")) {

                throw new AppException(
                        ErrorCode.UNAUTHENTICATED);
            }

            // Bỏ "Bearer "
            token = token.substring(7);

            // Parse JWT
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Lấy jti
            String jwtId = claims.getJWTID();

            // Lấy thời gian hết hạn
            Date expirationTime = claims.getExpirationTime();

            // JWT phải có jti và expiration
            if (jwtId == null ||
                    expirationTime == null) {

                throw new AppException(
                        ErrorCode.UNAUTHENTICATED);
            }

            // BLACKLIST TOKEN
            tokenBlacklistService.revokeToken(
                    jwtId,
                    expirationTime);

            System.out.println(
                    "Logout token: " + jwtId);

            System.out.println(
                    "Token expires: " +
                            expirationTime);

        } catch (ParseException e) {

            throw new AppException(
                    ErrorCode.UNAUTHENTICATED);
        }
    }
}
