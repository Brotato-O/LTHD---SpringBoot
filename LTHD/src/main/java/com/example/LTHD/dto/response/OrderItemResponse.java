package com.example.LTHD.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemResponse {

    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
