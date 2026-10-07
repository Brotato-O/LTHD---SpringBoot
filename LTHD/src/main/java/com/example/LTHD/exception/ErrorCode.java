package com.example.LTHD.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    PRODUCT_NOT_FOUND(2006, "Không tìm thấy sản phẩm"),
    INVALID_REQUEST(400, "Dữ liệu đầu vào không hợp lệ"),

    EMAIL_ALREADY_EXISTS(3001, "Email đã tồn tại"),
    USER_NOT_FOUND(3002, "Không tìm thấy người dùng"),
    INVALID_CREDENTIALS(3003, "Email hoặc mật khẩu không chính xác"),
    ROLE_NOT_FOUND(3004, "Không tìm thấy vai trò"),
    ACCOUNT_DISABLED(3005, "Tài khoản đã bị khóa");

    private final int code;
    private final String message;
}