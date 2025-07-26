package com.example.ecommerce.service.customer;

import com.example.ecommerce.dto.CustomerDto;
import com.example.ecommerce.enums.Status;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.mapper.CustomerMapper;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.VerificationTokenRepository;
import com.example.ecommerce.service.otp_email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
class CustomerRegistrationServiceTest {

    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;
    private PasswordEncoder passwordEncoder;
    private CustomerRegistrationService service;
    private Validator validator;
    private EmailService emailService;
    private VerificationTokenRepository verificationTokenRepository;


    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerMapper = mock(CustomerMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        validator = mock(Validator.class);
        emailService = mock(EmailService.class);
        verificationTokenRepository = mock(VerificationTokenRepository.class);
        service = new CustomerRegistrationService(customerRepository, customerMapper, passwordEncoder,validator,emailService,verificationTokenRepository);
    }

    @Test
    void shouldRegisterNewCustomerSuccessfully() {
        CustomerDto dto = new CustomerDto();
        dto.setEmail("new@example.com");
        dto.setUsername("newuser");
        dto.setPassword("PlainText");

        when(customerRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(customerRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("PlainText")).thenReturn("hashedPassword");

        Customer mappedCustomer = new Customer();
        when(customerMapper.toEntity(dto)).thenReturn(mappedCustomer);
        when(customerRepository.save(mappedCustomer)).thenReturn(mappedCustomer);

        Customer result = service.registerCustomer(dto);

        assertEquals(Status.DEACTIVATED, result.getAccountStatus());
        verify(customerRepository).save(mappedCustomer);
    }

    @Test
    void shouldThrowExceptionIfEmailExists() {
        CustomerDto dto = new CustomerDto();
        dto.setEmail("existing@example.com");

        when(customerRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }

    @Test
    void shouldThrowExceptionIfUsernameExists() {
        CustomerDto dto = new CustomerDto();
        dto.setEmail("unique@example.com");
        dto.setUsername("existinguser");

        when(customerRepository.existsByEmail("unique@example.com")).thenReturn(false);
        when(customerRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }
    @Test
    void shouldRegisterWithStrongPassword() {
        CustomerDto dto = new CustomerDto("test_user", "Test1234!", "test@example.com");
        Customer customer = new Customer();
        when(customerRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(customerRepository.existsByUsername("test_user")).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashed");
        when(customerMapper.toEntity(dto)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer result = service.registerCustomer(dto);

        assertNotNull(result);
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldThrowForNullPassword() {
        CustomerDto dto = new CustomerDto("test_user", null, "test@example.com");
        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }

    @Test
    void shouldThrowForNullUsername() {
        CustomerDto dto = new CustomerDto(null, "Test1234!", "test@example.com");
        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }

    @Test
    void shouldThrowForUsernameWithInvalidSymbols() {
        CustomerDto dto = new CustomerDto("test@user", "Test1234!", "test@example.com");
        // Assuming validation is added before calling service or inside the service
        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }

    @Test
    void shouldThrowForUsernameWithSpaces() {
        CustomerDto dto = new CustomerDto("test user", "Test1234!", "test@example.com");
        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }

    @Test
    void shouldThrowForInvalidEmail() {
        CustomerDto dto = new CustomerDto("test_user", "Test1234!", "invalid-email");
        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }

    @Test
    void shouldThrowForWeakPassword() {
        CustomerDto dto = new CustomerDto("test_user", "abc", "test@example.com");
        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }

    @Test
    void shouldThrowForShortPassword() {
        CustomerDto dto = new CustomerDto("test_user", "T1!", "test@example.com");
        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }

    @Test
    void shouldThrowForCommonPatternPassword() {
        CustomerDto dto = new CustomerDto("test_user", "1111", "test@example.com");
        assertThrows(InvalidInputException.class, () -> service.registerCustomer(dto));
    }
}
