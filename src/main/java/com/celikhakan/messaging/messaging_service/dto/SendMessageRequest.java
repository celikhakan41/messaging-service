package com.celikhakan.messaging.messaging_service.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String to;
    private String content;
}
