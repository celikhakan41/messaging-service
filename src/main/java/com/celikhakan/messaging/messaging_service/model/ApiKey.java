package com.celikhakan.messaging.messaging_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "api_keys")
public class ApiKey {
    @Id
    private String id;
    private String key;
    private String username;
    private String tenantId;
    private LocalDateTime createdAt;
}
