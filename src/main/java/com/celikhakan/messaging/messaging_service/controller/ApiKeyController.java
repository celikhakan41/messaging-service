package com.celikhakan.messaging.messaging_service.controller;

import com.celikhakan.messaging.messaging_service.dto.ApiKeyResponse;
import com.celikhakan.messaging.messaging_service.model.ApiKey;
import com.celikhakan.messaging.messaging_service.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping("/generate")
    public ResponseEntity<ApiKeyResponse> generateApiKey(Authentication authentication) {
        String username = authentication.getName();
        ApiKey apiKey = apiKeyService.generateApiKey(username);
        ApiKeyResponse response = new ApiKeyResponse(apiKey.getKey(), apiKey.getCreatedAt());
        return ResponseEntity.ok(response);
    }
}
