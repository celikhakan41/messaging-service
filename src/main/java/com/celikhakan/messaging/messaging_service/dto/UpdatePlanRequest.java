package com.celikhakan.messaging.messaging_service.dto;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import lombok.Data;

/**
 * Request body for updating a tenant's subscription plan.
 */
@Data
public class UpdatePlanRequest {
    private PlanType planType;
}
