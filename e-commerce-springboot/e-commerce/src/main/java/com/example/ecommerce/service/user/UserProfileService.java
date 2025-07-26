package com.example.ecommerce.service.user;

import org.springframework.stereotype.Service;
import ch.qos.logback.classic.Logger;

import com.example.ecommerce.model.Admin;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.PasswordResetToken;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.AdminRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.PasswordResetTokenRepository;
import com.example.ecommerce.service.otp_email.EmailService;
import com.example.ecommerce.validation.InputValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final PasswordResetTokenRepository tokenRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final CustomerRepository customerRepository;

    private final AdminRepository adminRepository;


    //FIRST: User Requests the RESET for Password//
    //RESET LINK is sent//
    public void requestPasswordReset(String username) {
        log.info("Password reset requested for username: {}", username);
        Optional<? extends User> optionalUser = findUserByUsername(username);
        if (optionalUser.isEmpty()) {
            log.warn("Password reset requested for non-existent username: {}", username);
            throw new UsernameNotFoundException("No user found with username: " + username);
        }

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                user.getEmail(),
                LocalDateTime.now().plusMinutes(15)
        );
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:8080/api/user/reset-password?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                "Reset Your Password",
                "Click the link to reset your password: " + resetLink
        );
        log.info("Password reset email sent to: {}", user.getEmail());
    }

    //SECOND: The ACTUAL reset//
    public void resetPassword(String token, String newPassword) {
        log.info("Attempting to reset password using token: {}", token);
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Invalid or expired token used: {}", token);
                    return new IllegalArgumentException("Invalid or expired token.");
                });
        //Expired Token HANDLER//
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Expired token used: {}", token);
            throw new IllegalArgumentException("Token has expired.");
        }
        //Non-Existent user HANDLER//
        Optional<? extends User> optionalUser = findUserByEmail(resetToken.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("No user associated with token email: {}", resetToken.getEmail());
            throw new UsernameNotFoundException("User not found.");
        }
        if (!InputValidator.isValidPassword(newPassword)) {
            log.warn("Password reset failed due to weak password format.");
            throw new IllegalArgumentException("Password must be at least 8 characters long, contain uppercase and lowercase letters, and include a digit.");
        }
        //New Password ENCODER//
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));

        // Each type of user is saved in their Repo//
        if (user instanceof Customer customer) {
            customerRepository.save(customer);
            log.info("Password reset successful for customer: {}", customer.getUsername());
        } else if (user instanceof Admin admin) {
            adminRepository.save(admin);
            log.info("Password reset successful for admin: {}", admin.getUsername());
        }

        tokenRepository.delete(resetToken);
        log.info("Reset token deleted after successful password reset.");
    }

    // Search for existing user by username
    private Optional<? extends User> findUserByUsername(String username) {
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) return customer;
        return adminRepository.findByUsername(username);
    }

    // Search for existing user by email (used for password reset)
    private Optional<? extends User> findUserByEmail(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) return customer;
        return adminRepository.findByEmail(email);
    }
}


/***
 * User dashboard and data fetching
 ***/