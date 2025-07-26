package com.example.ecommerce.service.user;

import com.example.ecommerce.model.LoginAttempt;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.LoginAttemptRepository;
import com.example.ecommerce.validation.InputValidator;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public final class UserLoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final int EXPIRATION_MINUTES = 60;

    private final LoginAttemptRepository loginAttemptRepository;
    private final CustomerRepository customerRepository;

    private final Cache<Long, Integer> attemptCache = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRATION_MINUTES, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private final Cache<String, Long> userIdCache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();

    public void loginFailed(String key) {
        Long userId = getCachedUserId(key);
        if (userId == null) return;

        int attempts = Optional.ofNullable(attemptCache.getIfPresent(userId)).orElse(0) + 1;
        attemptCache.put(userId, attempts);

        LoginAttempt attempt = loginAttemptRepository.findById(userId)
                .orElse(new LoginAttempt(userId, 0, null));

        attempt.setAttempts(attempts);
        attempt.setLastAttempt(LocalDateTime.now());

        loginAttemptRepository.save(attempt);
    }

    public void loginSucceeded(String key) {
        Long userId = getCachedUserId(key);
        if (userId == null) return;

        attemptCache.invalidate(userId);
        loginAttemptRepository.deleteById(userId);
    }

    public boolean isBlocked(String key) {
        Long userId = getCachedUserId(key);
        if (userId == null) return false;

        Integer attempts = attemptCache.getIfPresent(userId);
        if (attempts == null) {
            Optional<LoginAttempt> attempt = loginAttemptRepository.findById(userId);
            if (attempt.isPresent()) {
                attempts = attempt.get().getAttempts();
                attemptCache.put(userId, attempts);
            }
        }

        return attempts != null && attempts >= MAX_ATTEMPTS;
    }

    private Long getCachedUserId(String key) {
        Long cachedId = userIdCache.getIfPresent(key);
        if (cachedId != null) return cachedId;

        Optional<Long> userIdOpt = findUserIdByKey(key);
        userIdOpt.ifPresent(id -> userIdCache.put(key, id));
        return userIdOpt.orElse(null);
    }

    public Optional<Long> findUserIdByKey(String key) {
        if (InputValidator.isValidEmail(key)) {
            return customerRepository.findByUsernameOrEmail(null, key)
                    .map(User::getId);
        } else {
            return customerRepository.findByUsernameOrEmail(key, null)
                    .map(User::getId);
        }
    }

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void cleanupOldAttempts() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(EXPIRATION_MINUTES);
        loginAttemptRepository.deleteAllByLastAttemptBefore(expirationThreshold);
    }
}
