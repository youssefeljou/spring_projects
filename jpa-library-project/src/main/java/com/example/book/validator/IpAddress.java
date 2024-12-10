package com.example.book.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom annotation for validating IP addresses. This annotation can be applied
 * to fields that need to be validated as proper IP addresses. It uses the
 * {@link IpAddressImpl} class to define the validation logic.
 */
@Documented
@Constraint(validatedBy = { IpAddressImpl.class }) // Specifies the validator class responsible for the validation logic
@Target({ FIELD }) // Indicates this annotation can be applied to fields
@Retention(RUNTIME) // Retained at runtime for reflection purposes
public @interface IpAddress {

	/**
	 * Default error message when validation fails.
	 *
	 * @return the default message
	 */
	String message() default "{validation.constraints.ip-address.message}";

	/**
	 * Groups for validation categorization.
	 *
	 * @return the default groups
	 */
	Class<?>[] groups() default {};

	/**
	 * Additional data associated with the validation.
	 *
	 * @return the default payload
	 */
	Class<? extends Payload>[] payload() default {};
}
