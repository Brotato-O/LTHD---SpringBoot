package com.example.LTHD.service;

import com.example.LTHD.common.Role;
import com.example.LTHD.dto.request.UserCreateRequest;
import com.example.LTHD.dto.request.UserUpdateRequest;
import com.example.LTHD.dto.response.UserResponse;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.UserMapper;
import com.example.LTHD.repository.UserRepository;
import com.example.LTHD.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void createUser_shouldEncodePasswordAndDefaultRoleToCustomer() {
        UserCreateRequest request = new UserCreateRequest(
                "alice", "plain-password", "alice@example.com", "Alice", null);
        User user = new User("alice", "encoded-password", "alice@example.com", "Alice", Role.CUSTOMER);
        UserResponse response = new UserResponse();

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = userService.createUser(request);

        assertThat(result).isSameAs(response);
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldRejectDuplicateUsername() {
        UserCreateRequest request = new UserCreateRequest(
                "alice", "password", "alice@example.com", "Alice", Role.CUSTOMER);
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USERNAME_EXISTED);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldRejectDuplicateEmail() {
        UserCreateRequest request = new UserCreateRequest(
                "alice", "password", "alice@example.com", "Alice", Role.CUSTOMER);
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_EXISTED);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUsers_shouldReturnResponses() {
        User user1 = new User("alice", "pw1", "alice@example.com", "Alice", Role.CUSTOMER);
        User user2 = new User("bob", "pw2", "bob@example.com", "Bob", Role.ADMIN);
        UserResponse response1 = new UserResponse();
        UserResponse response2 = new UserResponse();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toUserResponse(user1)).thenReturn(response1);
        when(userMapper.toUserResponse(user2)).thenReturn(response2);

        List<UserResponse> result = userService.getUsers();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(response1, response2);
    }

    @Test
    void getUsers_shouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.getUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void getUserById_shouldReturnUserResponse() {
        User user = new User("alice", "pw", "alice@example.com", "Alice", Role.CUSTOMER);
        UserResponse response = new UserResponse();
        response.setUsername("alice");
        response.setEmail("alice@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isSameAs(response);
        assertThat(result.getUsername()).isEqualTo("alice");
    }

    @Test
    void getUserById_shouldRejectMissingUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void updateUser_shouldRejectMissingUser() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldRejectDuplicateUsername() {
        User user = new User("oldname", "pw", "alice@example.com", "Alice", Role.CUSTOMER);
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("newname");
        request.setEmail("alice@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newname")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USERNAME_EXISTED);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldRejectDuplicateEmail() {
        User user = new User("alice", "pw", "old@example.com", "Alice", Role.CUSTOMER);
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("alice");
        request.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_EXISTED);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateAndReturnResponse() {
        User user = new User("alice", "pw", "alice@example.com", "Alice", Role.CUSTOMER);
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("alice2");
        request.setEmail("alice2@example.com");
        request.setFullName("Alice 2");

        UserResponse response = new UserResponse();
        response.setUsername("alice2");
        response.setEmail("alice2@example.com");
        response.setFullName("Alice 2");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("alice2")).thenReturn(false);
        when(userRepository.existsByEmail("alice2@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        UserResponse result = userService.updateUser(1L, request);

        assertThat(result).isSameAs(response);
        verify(userMapper).updateUser(user, request);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_shouldRejectMissingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        User user = new User("alice", "pw", "alice@example.com", "Alice", Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}
