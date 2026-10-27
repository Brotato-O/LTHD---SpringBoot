package com.example.LTHD.controller;

import com.example.LTHD.dto.response.PaymentResponse;
import com.example.LTHD.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Lấy thông tin thanh toán theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse getPaymentById(
            @PathVariable Long id) {

        return paymentService.getPaymentById(id);
    }

    // Lấy thông tin thanh toán theo đơn hàng
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse getPaymentByOrderId(
            @PathVariable Long orderId) {

        return paymentService.getPaymentByOrderId(orderId);
    }

    // Admin xác nhận đã thu tiền COD
    @PutMapping("/order/{orderId}/confirm-cod")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse confirmCODPayment(
            @PathVariable Long orderId) {

        return paymentService.confirmCODPayment(orderId);
    }
}