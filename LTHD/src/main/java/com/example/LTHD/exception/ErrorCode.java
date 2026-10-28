
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
        PRODUCT_MODIFIED(
                        409,
                        "Sản phẩm đã được cập nhật bởi người khác. Vui lòng tải lại dữ liệu.",
                        HttpStatus.CONFLICT),

        // Cart
        CART_ITEM_NOT_FOUND(404, "Không tìm thấy sản phẩm trong giỏ", HttpStatus.NOT_FOUND),
        CART_NOT_FOUND(404, "Không tìm thấy giỏ hàng", HttpStatus.NOT_FOUND),
        CART_EMPTY(400, "Giỏ hàng đang trống", HttpStatus.BAD_REQUEST),

        ORDER_NOT_FOUND(404, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),

        ORDER_CANNOT_CANCEL(400, "Chỉ có thể hủy đơn hàng đang chờ xác nhận", HttpStatus.BAD_REQUEST),

        ORDER_INVALID_STATUS(400, "Trạng thái đơn hàng không hợp lệ", HttpStatus.BAD_REQUEST),

        ORDER_CANNOT_UPDATE(400, "Không thể cập nhật đơn hàng đã kết thúc", HttpStatus.BAD_REQUEST),

        ORDER_ACCESS_DENIED(403, "Bạn không có quyền truy cập đơn hàng này", HttpStatus.FORBIDDEN),

        // Checkout
        INVALID_QUANTITY(400, "Số lượng sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),

        INSUFFICIENT_STOCK(400, "Số lượng sản phẩm trong kho không đủ", HttpStatus.BAD_REQUEST),

        INVALID_PAYMENT_METHOD(400, "Phương thức thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),

        // Payment
        PAYMENT_NOT_FOUND(404, "Không tìm thấy thông tin thanh toán", HttpStatus.NOT_FOUND),

        PAYMENT_ALREADY_PAID(400, "Đơn hàng đã được thanh toán", HttpStatus.BAD_REQUEST),

        PAYMENT_FAILED(400, "Thanh toán thất bại", HttpStatus.BAD_REQUEST),

        PAYMENT_CANNOT_CONFIRM(400, "Không thể xác nhận thanh toán cho đơn hàng này", HttpStatus.BAD_REQUEST);

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
