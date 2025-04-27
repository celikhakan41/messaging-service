package com.celikhakan.messaging.messaging_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String sender;
    private String receiver;
    private String content;
    private String tenantId;
    private LocalDateTime timestamp;
}
