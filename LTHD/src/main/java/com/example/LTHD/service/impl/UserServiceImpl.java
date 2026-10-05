package com.example.LTHD.service.impl;

import com.example.LTHD.common.Role;
import com.example.LTHD.dto.request.UserCreateRequest;
import com.example.LTHD.dto.request.UserUpdateRequest;
import com.example.LTHD.dto.response.UserResponse;
import com.example.LTHD.entity.User;
import com.example.LTHD.exception.AppException;
import com.example.LTHD.exception.ErrorCode;
import com.example.LTHD.mapper.UserMapper;
import com.example.LTHD.repository.UserRepository;
import com.example.LTHD.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // DTO → Entity
        User user = userMapper.toUser(request);

        // Mã hóa password
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));

        // Mặc định CUSTOMER
        if (request.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        } else {
            user.setRole(request.getRole());
        }

        User savedUser = userRepository.save(user);

        // Entity → Response DTO
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(
            Long id,
            UserUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {

            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Request DTO → Entity đang tồn tại
        userMapper.updateUser(user, request);

        User updatedUser = userRepository.save(user);

        // Entity → Response DTO
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}