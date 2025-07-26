package com.example.ecommerce.controller;

import com.example.ecommerce.dto.*;

import com.example.ecommerce.enums.Category;
import com.example.ecommerce.dto.CustomerDto;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.customer.CustomerActivationService;
import com.example.ecommerce.service.customer.CustomerRegistrationService;
import com.example.ecommerce.service.customer.CustomerProfileService;

import com.example.ecommerce.service.customer.CustomerOrderHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer APIs", description = "Operations related to customer registration, verification, and interaction")
public class CustomerController {

    private final CustomerRegistrationService customerRegistrationService;
    private final CustomerActivationService customerActivationService;
    private final CustomerProfileService customerProfileService;

    //Constructor injections
    public CustomerController(CustomerRegistrationService customerRegistrationService, CustomerActivationService customerActivationService, CustomerProfileService customerProfileService) {
        this.customerRegistrationService = customerRegistrationService;
        this.customerActivationService = customerActivationService;
        this.customerProfileService = customerProfileService;
    }

    @Operation(summary = "Register a new customer", description = "Creates a new customer account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation failed"),
            @ApiResponse(responseCode = "409", description = "Email or username already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<Customer> register(@Valid @RequestBody CustomerDto customerDto) {
        Customer registeredCustomer = customerRegistrationService.registerCustomer(customerDto);
        return ResponseEntity.status(201).body(registeredCustomer);
    }

    @Operation(summary = "Search products", description = "Search for products by name and optionally by category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products found successfully"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid query parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public Set<Product> search(@RequestParam String name, @RequestParam(required = false) Category category) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Operation(summary = "View order history", description = "Fetch order history of a logged-in customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/order-history")
    public List<Order> previewOrderHistory() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Operation(summary = "Verify customer token", description = "Verify a customer account using a verification token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verify")
    public ResponseEntity<String> verifyCustomer(@RequestBody VerificationDto request) {
        String result = customerActivationService.verifyToken(request);
        return ResponseEntity.ok(result);
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerDto customerDto, HttpSession session) {
        return customerProfileService.updateCustomer(id, customerDto,session);
    }
}

