package com.example.ecommerce.service.review;

import com.example.ecommerce.dto.CreateReviewDto;
import com.example.ecommerce.dto.ReviewResponseDto;
import com.example.ecommerce.mapper.ReviewMapper;
import com.example.ecommerce.mapper.ReviewMapperImpl;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Review;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    private ReviewMapper reviewMapper;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        productRepository = mock(ProductRepository.class);
        customerRepository = mock(CustomerRepository.class);
        reviewMapper = spy(new ReviewMapperImpl());

        reviewService = new ReviewService(
                reviewRepository,
                productRepository,
                customerRepository,
                reviewMapper
        );
    }

    @Test
    void addReview_success() {
        CreateReviewDto dto = createDto();

        Product product = new Product();
        Customer customer = new Customer();
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        when(reviewRepository.findByCustomerIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponseDto response = reviewService.addReview(dto);

        assertEquals(dto.getRating(), response.getRating(), "Rating should match the input");
        assertEquals(dto.getComment(), response.getComment(), "Comment should match the input");
    }

    @Test
    void addReview_alreadyExists_throwsException() {
        CreateReviewDto dto = createDto();

        when(reviewRepository.findByCustomerIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(new Review()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> reviewService.addReview(dto));

        assertEquals("Customer already reviewed this product", exception.getMessage());
    }

    @Test
    void addReview_productNotFound_throwsException() {
        CreateReviewDto dto = createDto();

        when(reviewRepository.findByCustomerIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reviewService.addReview(dto));

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void addReview_customerNotFound_throwsException() {
        CreateReviewDto dto = createDto();

        when(reviewRepository.findByCustomerIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reviewService.addReview(dto));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void updateReview_success() {
        CreateReviewDto dto = createDto();
        dto.setRating(3);
        dto.setComment("Updated comment");

        Review existingReview = new Review();
        existingReview.setRating(1);
        existingReview.setComment("Old comment");

        when(reviewRepository.findByCustomerIdAndProductId(1L, 1L)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponseDto result = reviewService.updateReview(dto);

        assertEquals(3, result.getRating(), "Rating should be updated");
        assertEquals("Updated comment", result.getComment(), "Comment should be updated");
    }

    @Test
    void updateReview_notFound_throwsException() {
        CreateReviewDto dto = createDto();

        when(reviewRepository.findByCustomerIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(new Review()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> reviewService.addReview(dto));

        assertEquals("Customer already reviewed this product", exception.getMessage());
    }

    private CreateReviewDto createDto() {
        CreateReviewDto dto = new CreateReviewDto();
        dto.setCustomerId(1L);
        dto.setProductId(1L);
        dto.setRating(5);
        dto.setComment("Nice product!");
        return dto;
    }
}
