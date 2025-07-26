package com.example.ecommerce.dto;

import java.math.BigDecimal;

public record OrderItemViewDto(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        int quantity
) {}