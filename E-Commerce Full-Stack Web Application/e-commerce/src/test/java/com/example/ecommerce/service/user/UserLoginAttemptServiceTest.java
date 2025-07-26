package com.example.ecommerce.service.user;


import com.example.ecommerce.enums.Role;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.AdminRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.LoginAttemptRepository;
import com.example.ecommerce.session.AuthenticatedUserInfo;
import com.example.ecommerce.validation.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserLoginAttemptServiceTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AdminRepository adminRepository;


    private AuthenticatedUserInfo authenticatedUserInfo = new AuthenticatedUserInfo();

    @InjectMocks
    private UserLoginAttemptService loginAttemptService;

    @InjectMocks
    private CustomerAdminUserDetailsService customerAdminUserDetailsService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerAdminUserDetailsService = new CustomerAdminUserDetailsService(
                customerRepository, adminRepository, authenticatedUserInfo);
    }

    @Test
    void testLoginFailed_IncrementsAttempt() {
        String key = "john@example.com"; // email example
        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(userId);

        // Mock the repository call depending on whether key is email or username
        if (InputValidator.isValidEmail(key)) {
            when(customerRepository.findByUsernameOrEmail(null, key)).thenReturn(Optional.of(customer));
        } else {
            when(customerRepository.findByUsernameOrEmail(key, null)).thenReturn(Optional.of(customer));
        }
        when(loginAttemptRepository.findById(userId)).thenReturn(Optional.empty());

        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);

        assertTrue(loginAttemptService.isBlocked(key));
    }

    @Test
    void testLoginSucceeded_ClearsAttempts() {
        String key = "john@example.com"; // email example
        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(userId);

        if (InputValidator.isValidEmail(key)) {
            when(customerRepository.findByUsernameOrEmail(null, key)).thenReturn(Optional.of(customer));
        } else {
            when(customerRepository.findByUsernameOrEmail(key, null)).thenReturn(Optional.of(customer));
        }

        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginSucceeded(key);

        assertFalse(loginAttemptService.isBlocked(key));
    }

    @Test
    void testIsBlocked_returnsFalseForUnknownUser() {
        String key = "unknown@example.com";

        if (InputValidator.isValidEmail(key)) {
            when(customerRepository.findByUsernameOrEmail(null, key)).thenReturn(Optional.empty());
        } else {
            when(customerRepository.findByUsernameOrEmail(key, null)).thenReturn(Optional.empty());
        }

        assertFalse(loginAttemptService.isBlocked(key));
    }

    @Test
    void testLoadUserByEmail_customerExists_returnsCustomerUserDetails() {
        String email = "customer@example.com";
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setUsername("customerUser");
        customer.setEmail(email);
        customer.setPassword("pass123");
        customer.setRole(Role.CUSTOMER);

        // Mock adminRepository to return empty for findByUsernameOrEmail with (null, email)
        when(adminRepository.findByUsernameOrEmail(null, email)).thenReturn(Optional.empty());

        // Mock customerRepository to return the customer for findByUsernameOrEmail with (null, email)
        when(customerRepository.findByUsernameOrEmail(null, email)).thenReturn(Optional.of(customer));

        UserDetails userDetails = customerAdminUserDetailsService.loadUserByUsername(email);

        assertEquals("customerUser", userDetails.getUsername());
        assertEquals("pass123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));

        // Check if AuthenticatedUserInfo was populated
        assertEquals(2L, authenticatedUserInfo.getId());
        assertEquals("customerUser", authenticatedUserInfo.getUsername());
        assertEquals("CUSTOMER", authenticatedUserInfo.getRole());

        verify(adminRepository).findByUsernameOrEmail(null, email);
        verify(customerRepository).findByUsernameOrEmail(null, email);
    }


}

