package com.example.ecommerce.validation;

import com.example.ecommerce.service.customer.CustomerLoginService;
import com.example.ecommerce.service.user.UserAuthService;
import com.example.ecommerce.service.user.UserLoginAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static io.micrometer.common.util.StringUtils.isBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginValidator {

    private final UserLoginAttemptService userLoginAttemptService;
    private final CustomerLoginService customerLoginService;

    public String validate(String username, String email) {
        String key = !isBlank(username) ? username : !isBlank(email) ? email : null;

        if (key == null) {
            log.warn("Login failed: Both username and email are missing.");
            throw new IllegalArgumentException("You must provide either username or email.");
        }

        return key;
    }

    public void checkIfBlocked(String key) {
        if (userLoginAttemptService.isBlocked(key)) {
            log.warn("Login blocked due to too many failed attempts for key: {}", key);
            customerLoginService.updateStatusByKey(key, "SUSPENDED");
            throw new IllegalStateException("Too many failed login attempts. Please try again later.");
        }
    }

    public void handleLoginAttempt(String key, boolean success) {
        if (success) {
            userLoginAttemptService.loginSucceeded(key);
            log.info("Login successful for key: {}", key);
        } else {
            userLoginAttemptService.loginFailed(key);
            log.warn("Login failed: Invalid credentials for key: {}", key);
        }
    }

}