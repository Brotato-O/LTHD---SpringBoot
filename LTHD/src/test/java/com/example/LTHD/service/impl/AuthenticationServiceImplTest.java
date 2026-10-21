package com.example.LTHD.service.impl;

import com.example.LTHD.dto.request.AuthenticationRequest;
import com.example.LTHD.dto.response.AuthenticateResponse;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.repository.UserRepository;
import com.example.LTHD.service.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @InjectMocks
    private AuthenticationServiceImpl authService;

    @Test
    void authenticate_WrongPassword_ShouldThrowException() {
        // Gán key giả cho JWT để không bị lỗi NullPointerException
        ReflectionTestUtils.setField(authService, "SIGNER_KEY", "day-la-key-gia-de-test-1234567890");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("sai-mat-khau");

        User user = new User();
        // Pass đã mã hóa của "123456" (dùng BCrypt)
        user.setPassword("$2a$10$XQ7z... (chuỗi hash thật)");
        when(userRepo.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        // Kỳ vọng ném lỗi UNAUTHENTICATED
        AppException exception = assertThrows(AppException.class,
                () -> authService.authenticate(request));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }
}