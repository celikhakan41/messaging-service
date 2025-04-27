package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.dto.ChatMessageDTO;
import com.celikhakan.messaging.messaging_service.model.Message;
import com.celikhakan.messaging.messaging_service.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final MessageRepository messageRepository;

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(String json) {
        try {
            ChatMessageDTO dto = new ObjectMapper().readValue(json, ChatMessageDTO.class);
            logger.info("Kafka message received: {}", dto);

            Message message = Message.builder()
                    .sender(dto.getSender())
                    .receiver(dto.getReceiver())
                    .content(dto.getContent())
                    .timestamp(LocalDateTime.now())
                    .build();

            messageRepository.save(message);
        } catch (Exception e) {
            logger.error("Error while processing message: {}", e.getMessage());
        }
    }
}
