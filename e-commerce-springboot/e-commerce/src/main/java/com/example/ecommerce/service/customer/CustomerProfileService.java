package com.example.ecommerce.service.customer;

import com.example.ecommerce.dto.UpdateCustomerDto;
import com.example.ecommerce.mapper.CustomerMapper;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileService {
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    public ResponseEntity<Customer> updateCustomer(Long id, UpdateCustomerDto customerDto, HttpSession session) {
        // Validate that the provided customerDto is not null
        if (customerDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer data is required.");
        }

        // Retrieve the logged-in user's ID from the session
        Long loggedInUserId = (Long) session.getAttribute("userId");

        // Check if the logged-in user's ID matches the ID in the URL path
        if (!id.equals(loggedInUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile.");
        }

        // Check if customer with given ID exists
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with id " + id));

        // Check if the email or username is being changed
        boolean isEmailChanged = !customerDto.getEmail().equals(existingCustomer.getEmail());
        boolean isUsernameChanged = !customerDto.getUsername().equals(existingCustomer.getUsername());

        // If email has changed, check for uniqueness
        if (isEmailChanged && customerRepository.existsByEmail(customerDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use.");
        }

        // If username has changed, check for uniqueness
        if (isUsernameChanged && customerRepository.existsByUsername(customerDto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use.");
        }

        // Update fields of the existing customer only if the field is provided (not null or empty)
        if (customerDto.getUsername() != null && !customerDto.getUsername().isEmpty()) {
            existingCustomer.setUsername(customerDto.getUsername());
        }
        if (customerDto.getEmail() != null && !customerDto.getEmail().isEmpty()) {
            existingCustomer.setEmail(customerDto.getEmail());
        }
        if (customerDto.getName() != null && !customerDto.getName().isEmpty()) {
            existingCustomer.setName(customerDto.getName());
        }
        if (customerDto.getPhone() != null && !customerDto.getPhone().isEmpty()) {
            existingCustomer.setPhone(customerDto.getPhone());
        }

        // Save and return the updated customer with HTTP status 200 OK
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return ResponseEntity.ok(updatedCustomer);
    }

}

/***
 * view and edit customer data, link it with other services like orders,...
 ***/
