package com.example.ecommerce.service.customer;

import com.example.ecommerce.dto.UpdateCustomerDto;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerProfileServiceTest {

    private CustomerRepository customerRepository;
    private CustomerProfileService customerProfileService;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        session = mock(HttpSession.class);
        customerProfileService = new CustomerProfileService(null, customerRepository);
    }

    @Test
    void testUpdateCustomer_SuccessfulUpdate() {
        Long customerId = 1L;
        UpdateCustomerDto dto = new UpdateCustomerDto(
                customerId,
                "newuser",
                "newuser@example.com",
                "New User",
                Collections.singletonList("01011112222")
        );

        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setUsername("olduser");
        existingCustomer.setEmail("old@example.com");
        existingCustomer.setName("Old Name");
        existingCustomer.setPhone(Collections.singletonList("01000000000"));

        when(session.getAttribute("userId")).thenReturn(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(customerRepository.existsByUsername("newuser")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Customer> response = customerProfileService.updateCustomer(customerId, dto, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newuser", response.getBody().getUsername());
        assertEquals("newuser@example.com", response.getBody().getEmail());
    }

    @Test
    void testUpdateCustomer_NullDto_ThrowsException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                customerProfileService.updateCustomer(1L, null, session));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testUpdateCustomer_UnauthorizedUser_ThrowsException() {
        when(session.getAttribute("userId")).thenReturn(2L);
        UpdateCustomerDto dto = new UpdateCustomerDto(1L, "user", "user@example.com", "Name", Collections.singletonList("010"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                customerProfileService.updateCustomer(1L, dto, session));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testUpdateCustomer_CustomerNotFound_ThrowsException() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        UpdateCustomerDto dto = new UpdateCustomerDto(1L, "user", "user@example.com", "Name", Collections.singletonList("010"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                customerProfileService.updateCustomer(1L, dto, session));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testUpdateCustomer_EmailAlreadyExists_ThrowsException() {
        Long id = 1L;
        UpdateCustomerDto dto = new UpdateCustomerDto(id, "user", "new@example.com", "Name", Collections.singletonList("010"));
        Customer existingCustomer = new Customer();
        existingCustomer.setId(id);
        existingCustomer.setEmail("old@example.com");
        existingCustomer.setUsername("user");

        when(session.getAttribute("userId")).thenReturn(id);
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByEmail("new@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                customerProfileService.updateCustomer(id, dto, session));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().toLowerCase().contains("email"));
    }

    @Test
    void testUpdateCustomer_UsernameAlreadyExists_ThrowsException() {
        Long id = 1L;
        UpdateCustomerDto dto = new UpdateCustomerDto(id, "newuser", "user@example.com", "Name", Collections.singletonList("010"));
        Customer existingCustomer = new Customer();
        existingCustomer.setId(id);
        existingCustomer.setEmail("user@example.com");
        existingCustomer.setUsername("olduser");

        when(session.getAttribute("userId")).thenReturn(id);
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByUsername("newuser")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                customerProfileService.updateCustomer(id, dto, session));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().toLowerCase().contains("username"));
    }
}
