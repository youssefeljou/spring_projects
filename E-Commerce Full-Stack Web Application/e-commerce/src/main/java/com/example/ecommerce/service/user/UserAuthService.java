package com.example.ecommerce.service.user;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.ecommerce.enums.Status.DEACTIVATED;

/**
 * Service responsible for authenticating users by verifying their credentials.
 * It handles login, security checks, and password hashing verification.
 */
@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final CustomerAdminUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    /**
     * Authenticates a user based on their username or email and raw password.
     *
     * @param username    the username provided by the user (optional if email is provided)
     * @param email       the email provided by the user (optional if username is provided)
     * @param rawPassword the raw (plaintext) password to verify
     * @return true if the credentials match a registered user, false otherwise
     */
    public UserDetails authenticateAndGetUserDetails(String username, String email, String rawPassword) {
        String key = (username != null && !username.isBlank()) ? username : email;

        UserDetails userDetails = userDetailsService.loadUserByUsername(key); // may throw UsernameNotFoundException
        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            throw new IllegalArgumentException("Incorrect credentials.");
        }
        return userDetails;
    }

    public void checkCustomerStatus(String username, String email) {
        Optional<Customer> optionalCustomer = customerRepository.findByUsernameOrEmail(username, email);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if (customer.getAccountStatus() == DEACTIVATED) {
                throw new IllegalStateException("Customer account is deactivated.");
            }
        }
    }
}
