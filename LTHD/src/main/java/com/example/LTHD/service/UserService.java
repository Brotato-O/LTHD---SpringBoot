package com.example.LTHD.service;

import com.example.LTHD.dto.request.UserCreateRequest;
import com.example.LTHD.dto.request.UserUpdateRequest;
import com.example.LTHD.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getUsers();

    UserResponse getUserById(Long id);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);
}