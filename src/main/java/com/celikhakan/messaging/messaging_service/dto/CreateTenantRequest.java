package com.celikhakan.messaging.messaging_service.dto;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import lombok.Data;

/**
 * Request body for creating a new tenant with a specific subscription plan.
 */
@Data
public class CreateTenantRequest {
    private PlanType planType;
}
