package com.celikhakan.messaging.messaging_service.repository;

import com.celikhakan.messaging.messaging_service.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    /**
     * Retrieves messages between two users within the same tenant, ordered by timestamp asc.
     */
    List<Message> findByTenantIdAndSenderAndReceiverOrTenantIdAndReceiverAndSenderOrderByTimestampAsc(
            String tenantId1, String sender1, String receiver1,
            String tenantId2, String sender2, String receiver2
    );

    /**
     * Counts messages sent by a user within a tenant in the given time window.
     */
    long countByTenantIdAndSenderAndTimestampBetween(String tenantId, String sender,
                                                   LocalDateTime start, LocalDateTime end);
}
