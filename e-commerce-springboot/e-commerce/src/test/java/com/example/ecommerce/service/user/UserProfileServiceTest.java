package com.example.ecommerce.service.user;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;

import com.example.ecommerce.validation.InputValidator;

import com.example.ecommerce.service.otp_email.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AdminRepository adminRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * ✅ Valid Test
     * Tests that when a valid customer username is provided,
     * an email is sent and a token is saved.
     */

    @Test
    void requestPasswordReset_WithValidCustomerUsername_ShouldSendEmail() {
        Customer mockCustomer = new Customer();
        mockCustomer.setUsername("john");
        mockCustomer.setEmail("john@example.com");

        when(customerRepository.findByUsername("john")).thenReturn(Optional.of(mockCustomer));
        when(tokenRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        userProfileService.requestPasswordReset("john");

        verify(emailService).sendEmail(eq("john@example.com"), anyString(), contains("http://localhost:8080"));
        verify(tokenRepository).save(any(PasswordResetToken.class));
    }

    /**
     * ✅ Valid Test
     * Tests that when an unknown username is provided,
     * a UsernameNotFoundException is thrown.
     */

    @Test
    void requestPasswordReset_WithInvalidUsername_ShouldThrowException() {
        when(customerRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(adminRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userProfileService.requestPasswordReset("unknown"));
    }

    /**
     * ✅ Valid Test
     * Tests that when a valid token and strong password are provided,
     * the customer's password is reset and the token is deleted.
     */

    @Test
    void resetPassword_WithValidTokenAndPasswordForCustomer_ShouldResetPassword() {
        String token = UUID.randomUUID().toString();
        String email = "customer@example.com";

        PasswordResetToken resetToken = new PasswordResetToken(token, email, LocalDateTime.now().plusMinutes(10));
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setUsername("cust123");

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("ValidPass567")).thenReturn("encodedPwd");

        // ✅ MOCK THE STATIC VALIDATOR METHOD
        try (MockedStatic<InputValidator> mockedValidator = mockStatic(InputValidator.class)) {
            mockedValidator.when(() -> InputValidator.isValidPassword("ValidPass567"))
                    .thenReturn(true);

            userProfileService.resetPassword(token, "ValidPass567");

            verify(passwordEncoder).encode("ValidPass567");
            verify(customerRepository).save(customer);
            verify(tokenRepository).delete(resetToken);
        }
    }

    /**
     * ✅ Valid Test
     * Tests that if the reset token is expired, an IllegalArgumentException is thrown.
     */

    @Test
    void resetPassword_WithExpiredToken_ShouldThrowException() {
        PasswordResetToken expiredToken = new PasswordResetToken("token123", "email@test.com", LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(expiredToken));

        assertThrows(IllegalArgumentException.class, () -> userProfileService.resetPassword("token123", "ValidPass123"));
    }

    /**
     * ✅ Valid Test
     * Tests that if the reset token is not found in the repository,
     * an IllegalArgumentException is thrown.
     */

    @Test
    void resetPassword_WithInvalidToken_ShouldThrowException() {
        when(tokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userProfileService.resetPassword("bad-token", "ValidPass123"));
    }

    /**
     * ✅ Valid Test
     * Tests that if the new password is weak (e.g., too short),
     * an IllegalArgumentException is thrown.
     */

    @Test
    void resetPassword_WithWeakPassword_ShouldThrowException() {
        PasswordResetToken token = new PasswordResetToken("t123", "user@test.com", LocalDateTime.now().plusMinutes(10));
        Customer user = new Customer();
        user.setEmail("user@test.com");

        when(tokenRepository.findByToken("t123")).thenReturn(Optional.of(token));
        when(customerRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userProfileService.resetPassword("t123", "weak"));
    }

    /**
     * ✅ Valid Test
     * Tests that if the user (customer or admin) does not exist for the given token email,
     * a UsernameNotFoundException is thrown.
     */

    @Test
    void resetPassword_WithNonExistentUser_ShouldThrowException() {
        PasswordResetToken token = new PasswordResetToken("t123", "ghost@test.com", LocalDateTime.now().plusMinutes(10));
        when(tokenRepository.findByToken("t123")).thenReturn(Optional.of(token));
        when(customerRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userProfileService.resetPassword("t123", "ValidPass123"));
    }
}
