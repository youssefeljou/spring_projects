package com.example.ecommerce.dto;

import com.example.ecommerce.enums.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderViewDto(
        Long id,
        PaymentType paymentType,
        BigDecimal amount,
        String currency,
        LocalDate orderDate,
        List<OrderItemViewDto> items
) {}