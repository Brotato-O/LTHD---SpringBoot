package com.example.LTHD.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Integer id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Long version;

    private String imageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}