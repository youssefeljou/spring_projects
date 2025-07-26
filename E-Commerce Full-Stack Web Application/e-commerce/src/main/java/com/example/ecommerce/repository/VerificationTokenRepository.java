package com.example.ecommerce.repository;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    void deleteAllByExpiryDateBefore(LocalDateTime time);
    Optional<VerificationToken> findByTokenAndCustomer(String code, Customer customer);
}
