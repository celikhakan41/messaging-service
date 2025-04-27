package com.celikhakan.messaging.messaging_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private String sender;
    private String receiver;
    private String content;
    private String tenantId;
}
