package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PaymentRequestDto;
import com.example.ecommerce.dto.PaymentResultDto;
import com.example.ecommerce.enums.PaymentType;
import com.example.ecommerce.service.order.payment_service_strategy.PaymentStrategyFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final PaymentStrategyFactory factory;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{cartId}")
    public PaymentResultDto checkout(@PathVariable Long cartId,
                                     @RequestParam PaymentType method,
                                     @Valid @RequestBody(required = false) PaymentRequestDto body) {
        return factory.get(method).pay(cartId, body);
    }
}