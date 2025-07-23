package com.celikhakan.messaging.messaging_service.repository;

import com.celikhakan.messaging.messaging_service.model.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for CRUD operations on Tenant documents.
 */
public interface TenantRepository extends MongoRepository<Tenant, String> {
}
