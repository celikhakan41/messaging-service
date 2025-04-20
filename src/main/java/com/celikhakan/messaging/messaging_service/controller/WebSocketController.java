package com.celikhakan.messaging.messaging_service.controller;

import com.celikhakan.messaging.messaging_service.dto.ChatMessageDTO;
import com.celikhakan.messaging.messaging_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final KafkaProducerService kafkaProducer;

    @MessageMapping("/send")
    public void handleMessage(ChatMessageDTO message) {
        kafkaProducer.sendMessage("chat-messages", message);
    }
}
