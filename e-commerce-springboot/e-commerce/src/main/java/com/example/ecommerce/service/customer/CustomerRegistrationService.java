package com.example.ecommerce.service.customer;

import com.example.ecommerce.dto.CustomerDto;
import com.example.ecommerce.enums.Role;
import com.example.ecommerce.enums.Status;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.mapper.CustomerMapper;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.example.ecommerce.dto.CustomerDto;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.enums.Status;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.mapper.CustomerMapper;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.VerificationToken;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.VerificationTokenRepository;
import com.example.ecommerce.service.otp_email.EmailService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDateTime;


/***
 * register new customer to the system
 ***/



@Validated
@Slf4j
@Service
public class CustomerRegistrationService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;



    public CustomerRegistrationService(CustomerRepository customerRepository, CustomerMapper customerMapper, PasswordEncoder passwordEncoder,Validator validator,EmailService emailService,VerificationTokenRepository verificationTokenRepository) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;

    }

    public Customer registerCustomer(@Valid CustomerDto customerDto) {
        if (customerRepository.existsByEmail(customerDto.getEmail())) {
            throw new InvalidInputException("Email is already in use");
        }

        if (customerRepository.existsByUsername(customerDto.getUsername())) {
            throw new InvalidInputException("Username is already in use");
        }

        customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        Customer customer = customerMapper.toEntity(customerDto);

        if (customer != null) {
            customer.setAccountStatus(Status.DEACTIVATED);
            customer.setRole(Role.CUSTOMER);

            Customer savedCustomer = customerRepository.save(customer);

            //Generate verification code and expiry
            String verificationCode = String.valueOf((int)((Math.random() * 900000) + 100000));
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(2);

            //Create and save verification token
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(verificationCode);
            verificationToken.setExpiryDate(expiry);
            verificationToken.setCustomer(savedCustomer);
            verificationTokenRepository.save(verificationToken);

            //Send activation email
            emailService.sendEmail(
                    savedCustomer.getEmail(),
                    "Account Activation Code",
                    "Your activation code is: " + verificationCode
            );

            return savedCustomer;
        }

        throw new InvalidInputException("No customer created, invalid params entered");
    }

}


