package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.dto.ChatMessageDTO;
import com.celikhakan.messaging.messaging_service.dto.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final MessageService messageService;

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(String json) {
        try {
            ChatMessageDTO dto = new ObjectMapper().readValue(json, ChatMessageDTO.class);
            logger.info("Kafka message received: {}", dto);

            SendMessageRequest request = new SendMessageRequest();
            request.setTo(dto.getReceiver());
            request.setContent(dto.getContent());

            messageService.sendMessage(request, dto.getSender());
        } catch (Exception e) {
            logger.error("Error while processing message: {}", e.getMessage());
        }
    }
}
