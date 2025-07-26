package com.example.ecommerce.service.product;

import com.example.ecommerce.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ecommerce.model.Review;
import org.springframework.validation.annotation.Validated;


import java.util.List;


@Service
@Validated
public class ProductReviewService {
    private final ReviewRepository reviewRepository;

    public ProductReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public Float calculateAverageRating(Long productId) {
        List<Integer> ratings = reviewRepository.findByProductId(productId)
                .stream()
                .map(Review::getRating)
                .toList();

        return ratings.isEmpty() ? 0f :
                (float) ratings.stream().mapToInt(i -> i).average().orElse(0.0);
    }


}
