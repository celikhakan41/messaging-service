package com.celikhakan.messaging.messaging_service.dto;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO for transferring tenant configuration details over the API.
 */
@Getter
@Builder
public class TenantDto {
    private final String id;
    private final PlanType planType;
    private final LocalDateTime createdAt;
}
