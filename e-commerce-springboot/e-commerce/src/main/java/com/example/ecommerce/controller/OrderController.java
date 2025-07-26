package com.example.ecommerce.controller;

import com.example.ecommerce.enums.PaymentType;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.service.order.OrderManagementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderManagementService orderManagementService;

    //Constructor injections
    public OrderController(OrderManagementService orderManagementService) {
        this.orderManagementService = orderManagementService;
    }

    @PostMapping("/submit")
    public Order submitOrder(@RequestParam PaymentType paymentType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/pay")
    public boolean payOrder(@RequestBody Order order) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

