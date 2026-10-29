package com.example.LTHD.service;

import com.example.LTHD.common.Role;
import com.example.LTHD.dto.request.AuthenticationRequest;
import com.example.LTHD.dto.response.AuthenticateResponse;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.repository.UserRepository;
import com.example.LTHD.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    private static final String SIGNER_KEY =
            "e9e35c1f00258024870881315ca6bc1008fc4b5dca1813da0683b489a0e9cc2e";

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "SIGNER_KEY", SIGNER_KEY);
    }

    @Test
    void authenticate_shouldReturnSignedTokenForValidCredentials() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("alice@example.com");
        request.setPassword("password");
        User user = new User("alice", new BCryptPasswordEncoder().encode("password"),
                "alice@example.com", "Alice", Role.CUSTOMER);
        user.setId(7L);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        AuthenticateResponse result = authenticationService.authenticate(request);

        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getToken()).isNotBlank();
    }

    @Test
    void authenticate_shouldCreateVerifiableTokenWithExpectedClaims() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("alice@example.com");
        request.setPassword("password");
        User user = new User("alice", new BCryptPasswordEncoder().encode("password"),
                "alice@example.com", "Alice", Role.CUSTOMER);
        user.setId(7L);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        AuthenticateResponse result = authenticationService.authenticate(request);
        SignedJWT jwt = SignedJWT.parse(result.getToken());

        assertThat(jwt.getHeader().getAlgorithm()).isEqualTo(JWSAlgorithm.HS512);
        assertThat(jwt.verify(new MACVerifier(SIGNER_KEY.getBytes()))).isTrue();
        assertThat(jwt.getJWTClaimsSet().getSubject()).isEqualTo("alice");
        assertThat(jwt.getJWTClaimsSet().getLongClaim("userId")).isEqualTo(7L);
        assertThat(jwt.getJWTClaimsSet().getStringClaim("email"))
                .isEqualTo("alice@example.com");
        assertThat(jwt.getJWTClaimsSet().getStringClaim("fullName"))
                .isEqualTo("Alice");
        assertThat(jwt.getJWTClaimsSet().getStringClaim("role"))
                .isEqualTo("CUSTOMER");
        assertThat(jwt.getJWTClaimsSet().getIssuer()).isEqualTo("LTHD");
        assertThat(jwt.getJWTClaimsSet().getJWTID()).isNotBlank();
        assertThat(jwt.getJWTClaimsSet().getIssueTime()).isBeforeOrEqualTo(new Date());
        assertThat(jwt.getJWTClaimsSet().getExpirationTime()).isAfter(new Date());
    }

    @Test
    void authenticate_shouldRejectUnknownEmail() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("missing@example.com");
        request.setPassword("password");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void authenticate_shouldRejectWrongPassword() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("alice@example.com");
        request.setPassword("wrong-password");
        User user = new User("alice", new BCryptPasswordEncoder().encode("password"),
                "alice@example.com", "Alice", Role.CUSTOMER);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void logout_shouldRejectMalformedAuthorizationHeader() {
        assertThatThrownBy(() -> authenticationService.logout("invalid-token"))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void logout_shouldRevokeValidToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("alice@example.com");
        request.setPassword("password");
        User user = new User("alice", new BCryptPasswordEncoder().encode("password"),
                "alice@example.com", "Alice", Role.CUSTOMER);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        AuthenticateResponse loginResult = authenticationService.authenticate(request);
        SignedJWT jwt = SignedJWT.parse(loginResult.getToken());

        authenticationService.logout("Bearer " + loginResult.getToken());

        verify(tokenBlacklistService).revokeToken(
                jwt.getJWTClaimsSet().getJWTID(),
                jwt.getJWTClaimsSet().getExpirationTime());
    }

    @Test
    void logout_shouldRejectNullToken() {
        assertThatThrownBy(() -> authenticationService.logout(null))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void logout_shouldRejectBearerWithoutToken() {
        assertThatThrownBy(() -> authenticationService.logout("Bearer "))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verifyNoInteractions(tokenBlacklistService);
    }
}
