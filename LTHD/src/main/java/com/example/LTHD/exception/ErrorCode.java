package com.example.LTHD.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // User
    USER_NOT_FOUND(
            404,
            "User không tồn tại",
            HttpStatus.NOT_FOUND),

    USERNAME_EXISTED(
            400,
            "Username đã tồn tại",
            HttpStatus.BAD_REQUEST),

    EMAIL_EXISTED(
            400,
            "Email đã tồn tại",
            HttpStatus.BAD_REQUEST),

    // System
    INVALID_REQUEST(
            400,
            "Dữ liệu đầu vào không hợp lệ",
            HttpStatus.BAD_REQUEST),

    INTERNAL_SERVER_ERROR(
            500,
            "Lỗi hệ thống",
            HttpStatus.INTERNAL_SERVER_ERROR),

    UNAUTHENTICATED(
            400,
            "Đăng nhập thất bại",
            HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}