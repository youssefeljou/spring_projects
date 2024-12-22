package com.example.book.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

/**
 * Custom implementation of the AuditorAware interface. This class is used to
 * provide the current auditor's information for JPA auditing purposes.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

	/**
	 * Method to fetch the current auditor (the user performing the operation). In
	 * this case, it returns a hardcoded user "test_user".
	 *
	 * @return Optional containing the auditor's username
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		// This can be replaced with logic to fetch the real user's username from the
		// security context
		return Optional.of("test_user");
	}
}
