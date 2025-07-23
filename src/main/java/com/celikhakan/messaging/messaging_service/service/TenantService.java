package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import com.celikhakan.messaging.messaging_service.model.Tenant;
import com.celikhakan.messaging.messaging_service.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service layer for tenant lifecycle and configuration (plan type, metadata).
 */
@Service
@RequiredArgsConstructor
public class TenantService {
    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);
    private static final Clock CLOCK = Clock.systemUTC();

    private final TenantRepository tenantRepository;

    /**
     * Creates a new tenant with the given subscription plan.
     */
    public Tenant createTenant(PlanType planType) {
        String tenantId = UUID.randomUUID().toString();
        Tenant tenant = Tenant.builder()
                .id(tenantId)
                .planType(planType)
                .createdAt(LocalDateTime.now(CLOCK))
                .build();
        Tenant saved = tenantRepository.save(tenant);
        logger.info("Created new tenant {} with plan {}", saved.getId(), saved.getPlanType());
        return saved;
    }

    /**
     * Retrieves tenant configuration by its identifier.
     */
    public Tenant getTenant(String tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
    }

    /**
     * Updates the subscription plan for an existing tenant.
     */
    public Tenant updateTenantPlan(String tenantId, PlanType newPlan) {
        Tenant tenant = getTenant(tenantId);
        Tenant updated = Tenant.builder()
                .id(tenant.getId())
                .planType(newPlan)
                .createdAt(tenant.getCreatedAt())
                .build();
        Tenant saved = tenantRepository.save(updated);
        logger.info("Updated tenant {} plan to {}", tenantId, newPlan);
        return saved;
    }
}
