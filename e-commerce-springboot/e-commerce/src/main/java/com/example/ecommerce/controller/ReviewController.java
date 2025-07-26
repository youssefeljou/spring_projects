package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CreateReviewDto;
import com.example.ecommerce.dto.ReviewResponseDto;
import com.example.ecommerce.service.product.ProductReviewService;
import com.example.ecommerce.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Review APIs", description = "Endpoints for managing customer product reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductReviewService productReviewService;

    @Operation(
            summary = "Add a new review for a product",
            description = "Only authenticated customers can submit a product review",
            requestBody = @RequestBody(
                    description = "Review details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateReviewDto.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or review already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - customer not logged in"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Product or customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ReviewResponseDto> addReview(
            @RequestBody @Valid CreateReviewDto dto,
            @Parameter(hidden = true) HttpSession session) {

        // Get userId from session
        Long userId = (Long) session.getAttribute("userId");

        if (userId != dto.getCustomerId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null); // or custom error response
        }

        // Inject userId into DTO
        dto.setCustomerId(userId);

        // Proceed with saving review
        return ResponseEntity.ok(reviewService.addReview(dto));
    }

    @Operation(
            summary = "Update an existing review",
            description = "Only authenticated customers can update their review",
            requestBody = @RequestBody(
                    description = "Updated review details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateReviewDto.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - customer not logged in"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Review not found for this customer and product"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping
    public ResponseEntity<ReviewResponseDto> updateReview(
            @RequestBody @Valid CreateReviewDto dto,
            @Parameter(hidden = true) HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId != dto.getCustomerId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        dto.setCustomerId(userId);
        return ResponseEntity.ok(reviewService.updateReview(dto));
    }

    @Operation(
            summary = "Get all reviews for a specific product",
            description = "Returns a list of all reviews submitted for the given product ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of reviews retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long productId) {
        List<ReviewResponseDto> reviews = reviewService.getAllReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(
            summary = "Get average rating for a specific product",
            description = "Returns the average rating (float value) for all reviews of the specified product"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Average rating retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No reviews available for this product"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/average-rating")
    public ResponseEntity<Float> getAverageRating(@PathVariable Long productId) {
        Float averageRating = productReviewService.calculateAverageRating(productId);

        // If the product has no reviews, return a 404 status or 204 for no content
        if (averageRating == 0f) {
            return ResponseEntity.noContent().build();  // 204 No Content if no reviews exist
        }

        return ResponseEntity.ok(averageRating);  // 200 OK with the average rating
    }
}
