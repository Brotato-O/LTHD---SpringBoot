package com.example.LTHD.controller;

import com.example.LTHD.dto.request.UserCreateRequest;
import com.example.LTHD.dto.request.UserUpdateRequest;
import com.example.LTHD.dto.response.ApiResponse;
import com.example.LTHD.dto.response.UserResponse;
import com.example.LTHD.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Quản lý Người dùng", description = "Các API Thêm, Sửa, Xóa, Lấy thông tin Người dùng")
public class UserController {

        private final UserService userService;

        public UserController(
                        UserService userService) {
                this.userService = userService;
        }

        // GET ALL
        @Operation(summary = "Lấy danh sách tất cả người dùng", description = "Trả về một mảng danh sách users")
        @GetMapping
        public ApiResponse<List<UserResponse>> getUsers() {

                List<UserResponse> data = userService.getUsers();

                return new ApiResponse<>(
                                200,
                                "Lấy danh sách users thành công",
                                data);
        }

        // GET BY ID
        @Operation(summary = "Lấy thông tin user theo ID", description = "Trả về chi tiết của một user")
        @ApiResponses(value = { // <-- Thêm mô tả mã lỗi
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy user với ID này")
        })
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
        @Operation(summary = "Tạo người dùng mới", description = "Thêm một người dùng vào hệ thống") // <-- Thêm dòng
                                                                                                     // này
        @ApiResponses(value = { // <-- Thêm mô tả mã lỗi
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc trùng email/username")
        })
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
        @Operation(summary = "Cập nhật thông tin người dùng", description = "Cập nhật dữ liệu của user dựa vào ID") // <--
                                                                                                                    // Thêm
                                                                                                                    // dòng
                                                                                                                    // này
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
        @Operation(summary = "Xóa người dùng", description = "Xóa một user khỏi hệ thống dựa vào ID") // <-- Thêm dòng
                                                                                                      // này
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