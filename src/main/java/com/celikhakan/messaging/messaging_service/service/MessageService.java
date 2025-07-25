package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.dto.MessageResponse;
import com.celikhakan.messaging.messaging_service.dto.SendMessageRequest;
import com.celikhakan.messaging.messaging_service.model.Message;
import com.celikhakan.messaging.messaging_service.model.PlanType;
import com.celikhakan.messaging.messaging_service.model.User;
import com.celikhakan.messaging.messaging_service.repository.MessageRepository;
import com.celikhakan.messaging.messaging_service.config.AuthContext;
import com.celikhakan.messaging.messaging_service.repository.UserRepository;
import com.celikhakan.messaging.messaging_service.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private static final Clock CLOCK = Clock.systemUTC();

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TenantService tenantService;

    public MessageResponse sendMessage(SendMessageRequest request, String sender) {
        validateReceiverExists(request.getTo());
        User senderUser = getUserByUsername(sender);

        String tenantId = senderUser.getTenantId();
        PlanType planType = tenantService.getTenant(tenantId).getPlanType();
        if (PlanType.FREE.equals(planType)) {
            long todayMessageCount = messageRepository.countByTenantIdAndSenderAndTimestampBetween(
                    tenantId,
                    sender,
                    LocalDate.now(CLOCK).atStartOfDay(),
                    LocalDateTime.now(CLOCK)
            );
            if (todayMessageCount >= planType.getDailyMessageLimit()) {
                logger.warn("User '{}' has exceeded daily {} plan message limit.", sender, planType);
                throw new RuntimeException("Daily message limit exceeded for " + planType + " plan.");
            }
        }

        Message message = buildMessage(request, sender, senderUser.getTenantId());
        messageRepository.save(message);
        logger.info("{} sent message to {}: {}", sender, request.getTo(), request.getContent());

        return buildMessageResponse(message);
    }

    public List<MessageResponse> getConversation(String username, String currentUser) {
        validateReceiverExists(username);

        String tenantId = AuthContext.getTenantId()
                .orElseThrow(() -> new IllegalStateException("Tenant context not found"));
        List<Message> messages = messageRepository.findByTenantIdAndSenderAndReceiverOrTenantIdAndReceiverAndSenderOrderByTimestampAsc(
                tenantId, currentUser, username,
                tenantId, username, currentUser
        );

        logger.info("{} retrieved conversation with {}", currentUser, username);

        return messages.stream()
                .map(this::buildMessageResponse)
                .collect(Collectors.toList());
    }

    private void validateReceiverExists(String username) {
        if (!userRepository.existsByUsername(username)) {
            logger.warn("User '{}' does not exist.", username);
            throw new RuntimeException("User does not exist");
        }
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User '{}' not found.", username);
                    return new RuntimeException("User not found");
                });
    }

    private Message buildMessage(SendMessageRequest request, String sender, String tenantId) {
        return Message.builder()
                .sender(sender)
                .receiver(request.getTo())
                .content(request.getContent())
                .tenantId(tenantId)
                .timestamp(LocalDateTime.now(CLOCK))
                .build();
    }

    private MessageResponse buildMessageResponse(Message message) {
        return MessageResponse.builder()
                .from(message.getSender())
                .to(message.getReceiver())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }
}
