package com.example.LTHD.controller;

import com.example.LTHD.dto.request.UserCreateRequest;
import com.example.LTHD.dto.request.UserUpdateRequest;
import com.example.LTHD.dto.response.ApiResponse;
import com.example.LTHD.dto.response.UserResponse;
import com.example.LTHD.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {
        this.userService = userService;
    }

    // GET ALL
    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {

        List<UserResponse> data = userService.getUsers();

        return new ApiResponse<>(
                200,
                "Lấy danh sách users thành công",
                data);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(
            @PathVariable Long id) {

        UserResponse data = userService.getUserById(id);

        return new ApiResponse<>(
                200,
                "Lấy thông tin user thành công",
                data);
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) {

        UserResponse data = userService.createUser(request);

        return new ApiResponse<>(
                201,
                "Tạo user thành công",
                data);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse data = userService.updateUser(
                id,
                request);

        return new ApiResponse<>(
                200,
                "Cập nhật user thành công",
                data);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return new ApiResponse<>(
                200,
                "Xóa user thành công",
                null);
    }
}