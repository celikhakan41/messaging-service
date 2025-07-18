package com.celikhakan.messaging.messaging_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiKeyResponse {
    private final String apiKey;
    private final LocalDateTime createdAt;
}
