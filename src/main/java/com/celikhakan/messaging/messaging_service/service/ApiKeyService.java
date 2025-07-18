package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.model.ApiKey;
import com.celikhakan.messaging.messaging_service.model.User;
import com.celikhakan.messaging.messaging_service.repository.ApiKeyRepository;
import com.celikhakan.messaging.messaging_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private static final Clock CLOCK = Clock.systemUTC();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    public ApiKey generateApiKey(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String rawKey = UUID.randomUUID().toString() + UUID.randomUUID();
        String apiKey = Base64.getUrlEncoder().withoutPadding().encodeToString(rawKey.getBytes());
        ApiKey key = ApiKey.builder()
                .key(apiKey)
                .username(username)
                .tenantId(user.getTenantId())
                .createdAt(LocalDateTime.now(CLOCK))
                .build();
        return apiKeyRepository.save(key);
    }

    public ApiKey validateApiKey(String apiKey) {
        return apiKeyRepository.findByKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid API Key"));
    }
}
