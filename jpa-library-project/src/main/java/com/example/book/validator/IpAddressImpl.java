package com.example.book.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementation of the {@link ConstraintValidator} for the {@link IpAddress}
 * annotation. This class validates whether the given string is a valid IPv4
 * address.
 */
public class IpAddressImpl implements ConstraintValidator<IpAddress, String> {

	/**
	 * Validates if the given string is a valid IPv4 address.
	 * 
	 * @param value   the string to validate
	 * @param context the validation context
	 * @return true if the string is a valid IP address, false otherwise
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		// If the value is null or empty, consider it valid (can be customized based on
		// requirements)
		if (value == null || value.isEmpty()) {
			return true;
		}

		// Define the regex pattern for a valid IPv4 address
		Pattern pattern = Pattern.compile("^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$");
		Matcher matcher = pattern.matcher(value);

		try {
			// Check if the value matches the IPv4 format
			if (!matcher.matches()) {
				return false; // Return false if the format doesn't match
			} else {
				// Check if each octet (number between dots) is within valid IPv4 range (0-255)
				for (int i = 1; i <= 4; i++) {
					int octet = Integer.valueOf(matcher.group(i)); // Extract each octet
					if (octet > 255) {
						return false; // Return false if any octet is greater than 255
					}
				}
				return true; // Return true if all octets are valid
			}
		} catch (Exception e) {
			// In case of any exception during the validation process, return false
			return false;
		}
	}
}
