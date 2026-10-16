
package com.example.LTHD.exception;

import com.example.LTHD.dto.response.ApiResponse;

import jakarta.persistence.OptimisticLockException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler({
                        OptimisticLockException.class,
                        ObjectOptimisticLockingFailureException.class
        })
        public ResponseEntity<ApiResponse<Void>> handleOptimisticLockException(
                        Exception exception) {

                ErrorCode errorCode = ErrorCode.PRODUCT_MODIFIED;

                return ResponseEntity
                                .status(errorCode.getHttpStatus())
                                .body(new ApiResponse<>(
                                                errorCode.getCode(),
                                                errorCode.getMessage(),
                                                null));
        }

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

        // Bắt lỗi Bean Validation từ @Valid
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
                        MethodArgumentNotValidException exception) {

                Map<String, String> errors = new LinkedHashMap<>();

                exception.getBindingResult().getFieldErrors().forEach(error -> errors.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()));

                ApiResponse<Map<String, String>> response = new ApiResponse<>(
                                400,
                                "Dữ liệu đầu vào không hợp lệ",
                                errors);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // Bắt lỗi không có quyền truy cập
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
                        AccessDeniedException exception) {

                ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

                ApiResponse<Void> response = new ApiResponse<>(
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                null);

                return new ResponseEntity<>(
                                response,
                                errorCode.getHttpStatus());
        }

        // Bắt các lỗi không được phân loại
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleException(
                        Exception exception) {

                ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

                ApiResponse<Void> response = new ApiResponse<>(
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                null);

                return new ResponseEntity<>(
                                response,
                                errorCode.getHttpStatus());
        }
}