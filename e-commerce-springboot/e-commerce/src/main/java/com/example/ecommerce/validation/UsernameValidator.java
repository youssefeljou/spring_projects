package com.example.ecommerce.validation;

import com.example.ecommerce.validation.annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return InputValidator.isValidUsername(username);
    }
}
