package com.example.ecommerce.service.order.payment_service_strategy;

import com.example.ecommerce.enums.PaymentType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentStrategyFactory {
    private final CashPaymentService cashPaymentService;
    private final StripeCardPaymentService stripeCardPaymentService;

    private final Map<PaymentType, PaymentStrategy> strategyMap = new EnumMap<>(PaymentType.class);

    @PostConstruct
    public void init() {
        strategyMap.put(PaymentType.CASH, cashPaymentService);
        strategyMap.put(PaymentType.CARD, stripeCardPaymentService);
    }

    public PaymentStrategy get(PaymentType method) {
        PaymentStrategy strategy = strategyMap.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("No payment strategy for method: " + method);
        }
        return strategy;
    }
}