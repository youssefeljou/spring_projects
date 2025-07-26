
package com.example.ecommerce.service.order.payment_service_strategy;

import com.example.ecommerce.dto.PaymentRequestDto;
import com.example.ecommerce.dto.PaymentResultDto;

public interface PaymentStrategy  {
    public PaymentResultDto pay(Long cartId, PaymentRequestDto req);
}


