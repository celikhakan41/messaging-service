package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.dto.MessageResponse;
import com.celikhakan.messaging.messaging_service.dto.SendMessageRequest;
import com.celikhakan.messaging.messaging_service.model.Message;
import com.celikhakan.messaging.messaging_service.repository.MessageRepository;
import com.celikhakan.messaging.messaging_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public MessageResponse sendMessage(SendMessageRequest request, String sender) {
        if (!userRepository.existsByUsername(request.getTo())) {
            logger.warn("Message not sent. Receiver '{}' does not exist.", request.getTo());
            throw new RuntimeException("Receiver user does not exist");
        }

        Message message = Message.builder()
                .sender(sender)
                .receiver(request.getTo())
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        messageRepository.save(message);
        logger.info("{} sent message to {}: {}", sender, request.getTo(), request.getContent());

        return MessageResponse.builder()
                .from(sender)
                .to(request.getTo())
                .content(request.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }

    public List<MessageResponse> getConversation(String username, String currentUser) {
        if (!userRepository.existsByUsername(username)) {
            logger.warn("Conversation fetch failed. User '{}' does not exist.", username);
            throw new RuntimeException("The user you want to talk to does not exist");
        }

        List<Message> messages = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                currentUser, username, username, currentUser
        );

        logger.info("{} retrieved conversation with {}", currentUser, username);

        return messages.stream()
                .map(msg -> MessageResponse.builder()
                        .from(msg.getSender())
                        .to(msg.getReceiver())
                        .content(msg.getContent())
                        .timestamp(msg.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}
