package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AddToCartRequestDto;
import com.example.ecommerce.dto.UpdateCartRequestDto;
import com.example.ecommerce.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Shopping Cart APIs", description = "Manage customer shopping cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Operation(
            summary = "Add a product to the shopping cart",
            requestBody = @RequestBody(
                    required = true,
                    description = "Details of the product to add",
                    content = @Content(schema = @Schema(implementation = AddToCartRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product added to cart successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input (e.g., out of stock, discontinued)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Product or customer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add-to-cart")
    public ResponseEntity<String> addToShoppingCart(
            @Valid @org.springframework.web.bind.annotation.RequestBody AddToCartRequestDto request,
            @Parameter(hidden = true) HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        shoppingCartService.addProductToCart(userId, request);
        return ResponseEntity.ok("Product added to cart successfully");
    }

    @Operation(
            summary = "Update product quantity in the shopping cart",
            requestBody = @RequestBody(
                    required = true,
                    description = "Product and quantity to update",
                    content = @Content(schema = @Schema(implementation = UpdateCartRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cart updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input (e.g., quantity issues)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Customer, product, or cart not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/update-cart")
    public ResponseEntity<String> updateShoppingCart(
            @org.springframework.web.bind.annotation.RequestBody UpdateCartRequestDto request,
            @Parameter(hidden = true) HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        shoppingCartService.updateCart(userId, request);
        return ResponseEntity.ok("Cart updated successfully");
    }

    @Operation(
            summary = "Remove a product from the cart",
            parameters = @Parameter(name = "productId", description = "ID of the product to remove"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product deleted from cart successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or empty cart"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Customer or product not found in cart"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/remove-item")
    public ResponseEntity<String> removeFromShoppingCart(
            @RequestParam Long productId,
            @Parameter(hidden = true) HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        shoppingCartService.deleteProductFromCart(userId, productId);
        return ResponseEntity.ok("Product deleted from cart successfully");
    }

    @Operation(
            summary = "Clear all products from a shopping cart",
            parameters = @Parameter(name = "shoppingCartId", description = "ID of the shopping cart to clear"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Shopping cart cleared successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid cart state"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Cart or customer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/clear-cart")
    public ResponseEntity<String> clearShoppingCart(@RequestParam Long shoppingCartId) {
        shoppingCartService.clearCart(shoppingCartId);
        return ResponseEntity.ok("Shopping cart cleared successfully");
    }

    @Operation(
            summary = "Get the number of items in the cart",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns item count"),
                    @ApiResponse(responseCode = "501", description = "Not implemented yet")
            }
    )
    @GetMapping("/count")
    public int getItemsCount() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Operation(
            summary = "Calculate the total price of all items in the cart",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns total price"),
                    @ApiResponse(responseCode = "501", description = "Not implemented yet")
            }
    )
    @GetMapping("/total")
    public Float calculateTotal() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
