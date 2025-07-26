package com.example.ecommerce.service.product;

import com.example.ecommerce.model.Review;
import com.example.ecommerce.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ProductReviewService productReviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateAverageRating_shouldReturnCorrectAverage_whenRatingsExist() {
        Long productId = 1L;

        List<Review> reviews = List.of(
                createReviewWithRating(4),
                createReviewWithRating(5),
                createReviewWithRating(3)
        );

        when(reviewRepository.findByProductId(productId)).thenReturn(reviews);

        Float average = productReviewService.calculateAverageRating(productId);

        assertEquals(4.0f, average);
        verify(reviewRepository).findByProductId(productId);
    }

    @Test
    void calculateAverageRating_shouldReturnZero_whenNoReviews() {
        Long productId = 2L;

        when(reviewRepository.findByProductId(productId)).thenReturn(List.of());

        Float average = productReviewService.calculateAverageRating(productId);

        assertEquals(0f, average);
        verify(reviewRepository).findByProductId(productId);
    }

    @Test
    void calculateAverageRating_shouldReturnZero_whenAllRatingsAreZero() {
        Long productId = 3L;

        List<Review> reviews = List.of(
                createReviewWithRating(0),
                createReviewWithRating(0)
        );

        when(reviewRepository.findByProductId(productId)).thenReturn(reviews);

        Float average = productReviewService.calculateAverageRating(productId);

        assertEquals(0f, average);
    }

    private Review createReviewWithRating(int rating) {
        Review review = new Review();
        review.setRating(rating);
        return review;
    }
}
