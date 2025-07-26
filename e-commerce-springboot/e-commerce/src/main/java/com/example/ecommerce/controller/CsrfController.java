package com.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @Operation(
            summary = "Get CSRF token",
            description = "Retrieve CSRF token for frontend clients to include in requests"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSRF token retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/csrf-token")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
