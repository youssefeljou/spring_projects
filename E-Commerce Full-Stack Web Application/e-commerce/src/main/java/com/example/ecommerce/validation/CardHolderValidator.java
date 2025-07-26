package com.example.ecommerce.validation;

import com.example.ecommerce.validation.annotation.ValidCardholder;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CardHolderValidator implements ConstraintValidator<ValidCardholder,String> {
    @Override public boolean isValid(String v, ConstraintValidatorContext c) {
        return v != null && v.matches("[A-Za-z ]{2,40}");
    }
}

