package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    Optional<Review> findByCustomerIdAndProductId(Long customerId, Long productId);
}
