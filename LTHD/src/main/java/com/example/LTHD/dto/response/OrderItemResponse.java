package com.example.LTHD.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    private Long id;

    private Integer productId;

    private String productName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}