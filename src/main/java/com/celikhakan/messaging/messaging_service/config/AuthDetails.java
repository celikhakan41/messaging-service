package com.celikhakan.messaging.messaging_service.config;

import com.celikhakan.messaging.messaging_service.model.PlanType;

/**
 * A record to encapsulate authentication-specific details.
 * Records are ideal for immutable data carriers, automatically providing
 * constructor, getters, equals(), hashCode(), and toString().
 *
 * @param tenantId The identifier for the tenant associated with the authenticated user.
 * @param planType The subscription plan type of the authenticated user.
 * @param authType The type of authentication used (e.g., "JWT", "API_KEY").
 */
public record AuthDetails(
        String tenantId,
        PlanType planType,
        String authType
) { }