package com.example.ecommerce.service.review;

import com.example.ecommerce.dto.CreateReviewDto;
import com.example.ecommerce.dto.ReviewResponseDto;
import com.example.ecommerce.mapper.ReviewMapper;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Review;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ReviewMapper reviewMapper;


    @PreAuthorize("hasRole('CUSTOMER')")
    public ReviewResponseDto addReview(CreateReviewDto dto) {
        // check if already reviewed
        Optional<Review> existing = reviewRepository.findByCustomerIdAndProductId(dto.getCustomerId(), dto.getProductId());
        if (existing.isPresent()) {
            throw new IllegalStateException("Customer already reviewed this product");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Review review = reviewMapper.toEntity(dto);
        review.setProduct(product);
        review.setCustomer(customer);

        Review saved = reviewRepository.save(review);

        if (saved == null) {
            throw new IllegalStateException("Failed to save review");
        }
        // Return only rating and comment in response DTO
        return new ReviewResponseDto(saved.getRating(), saved.getComment());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public ReviewResponseDto updateReview(CreateReviewDto dto) {
        Review review = reviewRepository.findByCustomerIdAndProductId(dto.getCustomerId(), dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Review not found for this customer and product"));

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review updated = reviewRepository.save(review);

        if (updated == null) {
            throw new IllegalStateException("Failed to update review");
        }
        // Return only rating and comment in response DTO
        return new ReviewResponseDto(updated.getRating(), updated.getComment());
    }

    public List<ReviewResponseDto> getAllReviews(Long productId) {
        // Check if product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Fetch and return reviews for the product
        return reviewRepository.findByProductId(productId).stream()
                .map(review -> new ReviewResponseDto(review.getRating(), review.getComment()))
                .collect(Collectors.toList());
    }


}

