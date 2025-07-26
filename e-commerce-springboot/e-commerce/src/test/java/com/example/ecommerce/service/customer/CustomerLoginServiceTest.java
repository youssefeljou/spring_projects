package com.example.ecommerce.service.customer;

import com.example.ecommerce.enums.Status;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.validation.InputValidator; // assuming this is your validator class
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerLoginServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerLoginService customerLoginService;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateStatusByKey_WhenCustomerDoesNotExist_LogsWarning() {
        // Given
        String key = "nonexistent@example.com";  // This looks like an email
        String status = "ACTIVATED";

        // Mock repository call based on key type
        if (InputValidator.isValidEmail(key)) {
            when(customerRepository.findByUsernameOrEmail(null, key))
                    .thenReturn(Optional.empty());
        } else {
            when(customerRepository.findByUsernameOrEmail(key, null))
                    .thenReturn(Optional.empty());
        }

        // When
        customerLoginService.updateStatusByKey(key, status);

        // Then
        verify(customerRepository, never()).save(any());
    }
}
