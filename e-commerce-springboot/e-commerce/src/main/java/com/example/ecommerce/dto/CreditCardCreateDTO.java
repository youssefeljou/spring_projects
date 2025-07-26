package com.example.ecommerce.dto;

import com.example.ecommerce.validation.annotation.ValidCardNumber;
import com.example.ecommerce.validation.annotation.ValidCardholder;
import jakarta.validation.constraints.Pattern;

public record CreditCardCreateDTO(
        @ValidCardNumber String cardNumber,
        @ValidCardholder String cardHolderName,
        @Pattern(regexp="\\d{2}/\\d{2}", message="MM/YY") String expiration) {}


