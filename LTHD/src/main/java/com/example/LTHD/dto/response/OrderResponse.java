package com.example.LTHD.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;

    private Long userId;

    private BigDecimal totalAmount;

    private String shippingAddress;

    private String status;

    private String paymentMethod;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemResponse> items;

    private PaymentResponse payment;
}