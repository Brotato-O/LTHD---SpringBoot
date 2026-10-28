package com.example.LTHD.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;

    private String paymentMethod;

    private BigDecimal amount;

    private String status;

    private String transactionId;

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;
}