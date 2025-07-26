package com.example.ecommerce.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthServiceTest {

    @Mock
    private CustomerAdminUserDetailsService customerAdminUserDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.example.ecommerce.repository.CustomerRepository customerRepository;

    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject all required dependencies
        userAuthService = new UserAuthService(customerAdminUserDetailsService, passwordEncoder, customerRepository);
    }

    @Test
    void testAuthenticateAndGetUserDetails_withValidCredentials() {
        String username = "john";
        String rawPassword = "secret";
        String encodedPassword = "encodedSecret";

        UserDetails mockUserDetails = User.withUsername(username)
                .password(encodedPassword)
                .roles("CUSTOMER")
                .build();

        when(customerAdminUserDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        UserDetails result = userAuthService.authenticateAndGetUserDetails(username, null, rawPassword);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void testAuthenticateAndGetUserDetails_withInvalidPassword() {
        String username = "john";
        String rawPassword = "wrong";
        String encodedPassword = "encodedSecret";

        UserDetails mockUserDetails = User.withUsername(username)
                .password(encodedPassword)
                .roles("CUSTOMER")
                .build();

        when(customerAdminUserDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                userAuthService.authenticateAndGetUserDetails(username, null, rawPassword));
    }

    @Test
    void testAuthenticateAndGetUserDetails_userNotFound() {
        String email = "notfound@example.com";

        when(customerAdminUserDetailsService.loadUserByUsername(email))
                .thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () ->
                userAuthService.authenticateAndGetUserDetails(null, email, "password"));
    }
}
