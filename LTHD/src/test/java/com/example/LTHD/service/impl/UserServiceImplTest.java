package com.example.LTHD.service.impl;

import com.example.LTHD.common.Role;
import com.example.LTHD.dto.request.UserCreateRequest;
import com.example.LTHD.dto.response.UserResponse;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.UserMapper;
import com.example.LTHD.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    // ==========================================
    // KHU VỰC 1: TEST HÀM createUser
    // ==========================================

    @Test
    void createUser_ValidRequest_ShouldReturnUserResponse() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setEmail("new@gmail.com");
        request.setPassword("123456");

        User mockUser = new User();
        mockUser.setUsername("newuser");

        UserResponse mockResponse = new UserResponse();
        mockResponse.setUsername("newuser");
        mockResponse.setRole(Role.CUSTOMER);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toUser(any(UserCreateRequest.class))).thenReturn(mockUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(mockResponse);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("encodedPassword", mockUser.getPassword());
        assertEquals(Role.CUSTOMER, mockUser.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_UsernameExisted_ShouldThrowException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("existed_user");

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> userService.createUser(request));

        assertEquals(ErrorCode.USERNAME_EXISTED, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailExisted_ShouldThrowException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("new_user");
        request.setEmail("existed@gmail.com");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> userService.createUser(request));

        assertEquals(ErrorCode.EMAIL_EXISTED, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    // ==========================================
    // KHU VỰC 2: TEST HÀM getUserById
    // ==========================================

    @Test
    void getUserById_ValidId_ShouldReturnUser() {
        Long id = 1L;
        User mockUser = new User();
        mockUser.setId(id);
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserResponse(mockUser)).thenReturn(mockResponse);

        UserResponse result = userService.getUserById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getUserById_NotFound_ShouldThrowException() {
        Long id = 99L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> userService.getUserById(id));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    // ==========================================
    // BÀI TEST CỐ TÌNH SAI ĐỂ XEM BÁO LỖI
    // ==========================================
    @Test
    void test_Co_Tinh_Sai_De_Xem_Loi() {
        Long id = 1L;
        User mockUser = new User();
        mockUser.setId(id);

        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(id); // ID thực tế trả về là 1

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserResponse(mockUser)).thenReturn(mockResponse);

        UserResponse result = userService.getUserById(id);

        // TUI CỐ TÌNH KỲ VỌNG SAI:
        // Thực tế ID là 1, nhưng tui lại bắt nó phải bằng 99.
        assertEquals(99L, result.getId(), "ID trả về không khớp!");
    }
}
