package com.celikhakan.messaging.messaging_service.repository;

import com.celikhakan.messaging.messaging_service.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            String sender1, String receiver1, String sender2, String receiver2
    );

    long countBySenderAndTimestampBetween(String sender, LocalDateTime start, LocalDateTime end);
}
