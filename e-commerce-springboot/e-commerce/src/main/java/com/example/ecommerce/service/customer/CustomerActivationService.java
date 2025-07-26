package com.example.ecommerce.service.customer;

import com.example.ecommerce.dto.VerificationDto;
import com.example.ecommerce.enums.Status;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.VerificationToken;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CustomerActivationService {

    private  final CustomerRepository customerRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    public CustomerActivationService(JavaMailSender mailSender, CustomerRepository customerRepository,VerificationTokenRepository verificationTokenRepository)
    {

        this.customerRepository = customerRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }


    @Scheduled(fixedRate = 60 * 60 * 1000) // every hour
    public void removeExpiredTokens() {
        verificationTokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }


    public String verifyToken(VerificationDto verificationDto) {
        // 1. Find customer by email (or username)
        Customer customer = customerRepository.findByEmail(verificationDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Find the token associated with both token and customer
        VerificationToken verificationToken = verificationTokenRepository
                .findByTokenAndCustomer(verificationDto.getCode(), customer)
                .orElseThrow(() -> new RuntimeException("Invalid or mismatched token"));

        // 3. Check expiry
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new RuntimeException("Token expired");
        }

        // 4. Activate account
        customer.setAccountStatus(Status.ACTIVATED);
        customerRepository.save(customer);
        verificationTokenRepository.delete(verificationToken);

        return "Account verified successfully!";
    }



}
