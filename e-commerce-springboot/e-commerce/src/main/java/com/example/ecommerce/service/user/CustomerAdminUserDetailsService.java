package com.example.ecommerce.service.user;

import com.example.ecommerce.model.Admin;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.AdminRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.session.AuthenticatedUserInfo;
import com.example.ecommerce.validation.InputValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerAdminUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    @Getter
    private final AuthenticatedUserInfo authenticatedUserInfo;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        String email = InputValidator.isValidEmail(input) ? input : null;
        String username = email == null ? input : null;

        Optional<Admin> admin = adminRepository.findByUsernameOrEmail(username, email);
        if (admin.isPresent()) {
            Admin found = admin.get();
            authenticatedUserInfo.setId(found.getId());
            authenticatedUserInfo.setUsername(found.getUsername());
            authenticatedUserInfo.setRole("ADMIN");
            return buildUser(found.getUsername(), found.getPassword(), found.getRole().name());
        }

        Optional<Customer> customer = customerRepository.findByUsernameOrEmail(username, email);
        if (customer.isPresent()) {
            Customer found = customer.get();
            authenticatedUserInfo.setId(found.getId());
            authenticatedUserInfo.setUsername(found.getUsername());
            authenticatedUserInfo.setRole("CUSTOMER");
            return buildUser(found.getUsername(), found.getPassword(), found.getRole().name());
        }

        throw new UsernameNotFoundException("Username or email not found: " + input);
    }

    private UserDetails buildUser(String username, String password, String role) {
        return User.builder()
                .username(username)
                .password(password)
                .roles(role)
                .build();
    }
}
