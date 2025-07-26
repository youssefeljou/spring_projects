package com.example.ecommerce.repository;

import com.example.ecommerce.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    void deleteAllByLastAttemptBefore(LocalDateTime expirationThreshold);
}
