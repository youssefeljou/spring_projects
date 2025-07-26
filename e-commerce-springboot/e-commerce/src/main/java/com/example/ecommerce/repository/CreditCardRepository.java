package com.example.ecommerce.repository;

import com.example.ecommerce.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard,Long> {
    List<CreditCard> findByCustomerId(Long customerId);

    Optional<CreditCard> findByIdAndCustomerId(Long id, Long customerId);
}
