package com.example.education.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // This can be replaced with logic to fetch the real user's username from the
        // security context
        return Optional.of("test_user");
    }
}
