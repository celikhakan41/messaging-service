package com.celikhakan.messaging.messaging_service.controller;

import com.celikhakan.messaging.messaging_service.dto.ChatMessageDTO;
import com.celikhakan.messaging.messaging_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final KafkaProducerService kafkaProducer;

    @MessageMapping("/send")
    public void handleMessage(ChatMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        String tenantId = (String) headerAccessor.getSessionAttributes().get("tenantId");

        if (tenantId != null) {
            message.setTenantId(tenantId);
            kafkaProducer.sendMessage("chat-messages", message);
        } else {
            System.out.println("TenantId was not found, no message was sent.");
        }
    }
}
