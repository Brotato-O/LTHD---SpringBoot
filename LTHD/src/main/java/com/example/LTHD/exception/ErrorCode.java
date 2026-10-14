
package com.example.LTHD.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

        // User
        USER_NOT_FOUND(404, "User không tồn tại", HttpStatus.NOT_FOUND),

        USERNAME_EXISTED(400, "Username đã tồn tại", HttpStatus.BAD_REQUEST),

        EMAIL_EXISTED(400, "Email đã tồn tại", HttpStatus.BAD_REQUEST),

        // Validation
        INVALID_REQUEST(400, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),

        // Authentication
        UNAUTHENTICATED(401, "Bạn chưa được xác thực", HttpStatus.UNAUTHORIZED),
        UNAUTHORIZED(403, "Người dùng không có quyền truy cập", HttpStatus.FORBIDDEN),

        // System
        INTERNAL_SERVER_ERROR(500, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR),

        // Product
        PRODUCT_NOT_FOUND(404, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),

        // Cart
        CART_ITEM_NOT_FOUND(404, "Không tìm thấy sản phẩm trong giỏ", HttpStatus.NOT_FOUND);

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
