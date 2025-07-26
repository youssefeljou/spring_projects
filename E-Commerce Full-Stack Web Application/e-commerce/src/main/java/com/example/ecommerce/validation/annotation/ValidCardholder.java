package com.example.ecommerce.validation.annotation;

import com.example.ecommerce.validation.CardHolderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = CardHolderValidator.class)
public @interface ValidCardholder {
    String message() default "Card holder name must be letters & spaces only";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
