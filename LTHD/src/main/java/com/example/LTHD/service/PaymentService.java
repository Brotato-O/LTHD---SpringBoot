package com.example.LTHD.service;

import com.example.LTHD.dto.response.PaymentResponse;

public interface PaymentService {

    // Lấy thông tin thanh toán theo ID
    PaymentResponse getPaymentById(Long id);

    // Lấy thanh toán theo đơn hàng
    PaymentResponse getPaymentByOrderId(Long orderId);

    // Xác nhận đã thanh toán COD
    PaymentResponse confirmCODPayment(Long orderId);
}