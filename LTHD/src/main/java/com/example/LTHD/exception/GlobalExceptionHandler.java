package com.example.LTHD.exception;

import com.example.LTHD.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt các lỗi nghiệp vụ do hệ thống chủ động ném ra
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(
            AppException exception) {

        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<Void> response = new ApiResponse<>(
                errorCode.getCode(),
                errorCode.getMessage(),
                null);

        return new ResponseEntity<>(
                response,
                errorCode.getHttpStatus());
    }

    // Bắt lỗi Validation từ @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Dữ liệu đầu vào không hợp lệ");

        ApiResponse<Void> response = new ApiResponse<>(
                400,
                message,
                null);

        return new ResponseEntity<>(
                response,
                org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    // Bắt các lỗi không dự kiến
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception exception) {

        ApiResponse<Void> response = new ApiResponse<>(
                500,
                "Lỗi hệ thống",
                null);

        return new ResponseEntity<>(
                response,
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}