package com.example.ecommerce.validation;

import com.example.ecommerce.validation.annotation.ValidEmail;
import com.example.ecommerce.validation.annotation.ValidId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IdValidator  implements ConstraintValidator<ValidId, Long> {
    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        return InputValidator.isValidId(id);
    }
}
