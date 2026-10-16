package com.example.LTHD.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.0", message = "Price must be >= 0")
    private BigDecimal price;

    @NotNull(message = "Product stock is required")
    @PositiveOrZero(message = "Stock must be >= 0")
    private Integer stock;

    @NotNull(message = "Product version is required")
    private Long version;

    private String imageUrl;
}