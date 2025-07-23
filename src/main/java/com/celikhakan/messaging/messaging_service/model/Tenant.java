package com.celikhakan.messaging.messaging_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a tenant (customer) in the messaging service, including its subscription plan and metadata.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tenants")
public class Tenant {
    /**
     * Unique identifier for the tenant.
     */
    @Id
    private String id;

    /**
     * Subscription plan associated with the tenant.
     */
    private PlanType planType;

    /**
     * Creation timestamp of the tenant record.
     */
    private LocalDateTime createdAt;
}
