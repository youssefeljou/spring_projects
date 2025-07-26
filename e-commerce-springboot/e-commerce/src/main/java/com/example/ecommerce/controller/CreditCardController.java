package com.example.ecommerce.controller;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.service.customer.CreditCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/customers/{customerId}/cards")
public class CreditCardController {

    private final CreditCardService creditCardService;

    //--CREATE --
    @Operation(summary = "Add a new credit card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or card expired"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<CreditCardViewDTO> create(
            @PathVariable Long customerId,
            @Valid @RequestBody CreditCardCreateDTO body) {

        CreditCardViewDTO saved = creditCardService.addCard(customerId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    //--GET LIST--
    @Operation(summary = "Get all credit cards for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<CreditCardViewDTO>> list(@PathVariable Long customerId) {
        List<CreditCardViewDTO> cards = creditCardService.listCards(customerId);
        return ResponseEntity.ok(cards);
    }

    //--GET ONE--
    @Operation(summary = "Get a specific credit card by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("{cardId}")
    public ResponseEntity<CreditCardViewDTO> get(
            @PathVariable Long customerId,
            @PathVariable Long cardId) {

        CreditCardViewDTO card = creditCardService.getCard(customerId, cardId);
        return ResponseEntity.ok(card);
    }

    //--UPDATE--
    @Operation(summary = "Update a specific credit card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or card expired"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("{cardId}")
    public ResponseEntity<CreditCardViewDTO> update(
            @PathVariable Long customerId,
            @PathVariable Long cardId,
            @Valid @RequestBody CreditCardUpdateDTO body) {

        CreditCardViewDTO updated = creditCardService.updateCard(customerId, cardId, body);
        return ResponseEntity.ok(updated);
    }

    //--DELETE--
    @Operation(summary = "Delete a specific credit card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("{cardId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long customerId,
            @PathVariable Long cardId) {


        creditCardService.deleteCard(customerId, cardId);
        return ResponseEntity.noContent().build();
    }
}
