package com.example.ecommerce.dto;

import java.time.YearMonth;

public record CreditCardViewDTO(
        Long id,
        String last4,
        String brand,
        String cardHolderName,
        YearMonth expiration) {}

