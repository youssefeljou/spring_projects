package com.example.ecommerce.dto;

public record PaymentRequestDto(
        Long    cardId,
        String  number,
        String  cardHolderName,
        Integer expMonth,
        Integer expYear,
        String  cvc,
        boolean simulateFailure
) {}