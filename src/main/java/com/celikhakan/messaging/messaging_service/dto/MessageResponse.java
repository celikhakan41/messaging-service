package com.celikhakan.messaging.messaging_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private String from;
    private String to;
    private String content;
    private LocalDateTime timestamp;
}
