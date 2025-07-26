package com.example.ecommerce.service.customer;

import com.example.ecommerce.enums.Status;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.validation.InputValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerLoginService {
    private final CustomerRepository customerRepository;

    public void updateStatusByKey(String key, String status) {

        Optional<Customer> optionalCustomer;
        if (InputValidator.isValidEmail(key))
        {
             optionalCustomer = customerRepository.findByEmail(key);
        }
        else
        {
            optionalCustomer = customerRepository.findByUsername(key);
        }

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setAccountStatus(Status.valueOf(status));
            customerRepository.save(customer);
            log.info("Customer {} status updated to {}", key, status);
        } else {
            log.warn("No customer found with key: {}", key);
        }
    }
}
