package com.example.ecommerce.validation.annotation;


import com.example.ecommerce.validation.IdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IdValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidId {
    String message() default "Invalid Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
