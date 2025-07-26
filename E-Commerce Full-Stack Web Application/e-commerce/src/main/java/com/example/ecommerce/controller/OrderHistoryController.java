/* src/main/java/com/example/ecommerce/controller/OrderHistoryController.java */
package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderViewDto;
import com.example.ecommerce.service.order.OrderHistoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryService historyService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/history")
    public ResponseEntity<?> history(HttpSession session) {

        Long customerId = (Long) session.getAttribute("userId");
        if (customerId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");

        List<OrderViewDto> list = historyService.getHistory(customerId);
        return ResponseEntity.ok(list);
    }
}
