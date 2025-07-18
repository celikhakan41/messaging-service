package com.celikhakan.messaging.messaging_service.repository;

import com.celikhakan.messaging.messaging_service.model.ApiKey;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ApiKeyRepository extends MongoRepository<ApiKey, String> {
    Optional<ApiKey> findByKey(String key);
}
