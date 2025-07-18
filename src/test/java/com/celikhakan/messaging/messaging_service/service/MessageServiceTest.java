package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.dto.MessageResponse;
import com.celikhakan.messaging.messaging_service.dto.SendMessageRequest;
import com.celikhakan.messaging.messaging_service.model.Message;
import com.celikhakan.messaging.messaging_service.model.PlanType;
import com.celikhakan.messaging.messaging_service.model.User;
import com.celikhakan.messaging.messaging_service.repository.MessageRepository;
import com.celikhakan.messaging.messaging_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSendMessageSuccessfully() {
        SendMessageRequest request = new SendMessageRequest();
        request.setTo("receiver");
        request.setContent("Hello!");

        User senderUser = User.builder()
                .username("sender")
                .tenantId("tenant1")
                .build();

        when(userRepository.existsByUsername("receiver")).thenReturn(true);
        when(userRepository.findByUsername("sender")).thenReturn(java.util.Optional.of(senderUser));

        MessageResponse response = messageService.sendMessage(request, "sender");

        assertEquals("sender", response.getFrom());
        assertEquals("receiver", response.getTo());
        assertEquals("Hello!", response.getContent());

        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void shouldThrowExceptionWhenReceiverDoesNotExist() {
        SendMessageRequest request = new SendMessageRequest();
        request.setTo("nonexistent");
        request.setContent("Hey!");

        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> messageService.sendMessage(request, "sender"));
        verify(messageRepository, never()).save(any());
    }

    @Test
    void shouldReturnConversationBetweenUsers() {
        Message msg1 = Message.builder()
                .sender("user1")
                .receiver("user2")
                .content("Hi")
                .tenantId("tenant1")
                .timestamp(LocalDateTime.now())
                .build();

        Message msg2 = Message.builder()
                .sender("user2")
                .receiver("user1")
                .content("Hello")
                .timestamp(LocalDateTime.now())
                .build();

        when(userRepository.existsByUsername("user2")).thenReturn(true);
        when(messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                "user1", "user2", "user2", "user1"
        )).thenReturn(List.of(msg1, msg2));

        List<MessageResponse> responses = messageService.getConversation("user2", "user1");

        assertEquals(2, responses.size());
        assertEquals("Hi", responses.get(0).getContent());
        assertEquals("Hello", responses.get(1).getContent());
    }

    @Test
    void shouldThrowExceptionWhenOtherUserNotFound() {
        when(userRepository.existsByUsername("ghost")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> messageService.getConversation("ghost", "realUser"));
    }
    @Test
    void shouldThrowWhenFreePlanUserExceedsDailyLimit() {
        SendMessageRequest request = new SendMessageRequest();
        request.setTo("receiver");
        request.setContent("Hello again!");

        User senderUser = User.builder()
                .username("sender")
                .tenantId("tenant1")
                .planType(PlanType.FREE)
                .build();

        when(userRepository.existsByUsername("receiver")).thenReturn(true);
        when(userRepository.findByUsername("sender")).thenReturn(Optional.of(senderUser));
        when(messageRepository.countBySenderAndTimestampBetween(
                eq("sender"), any(), any())).thenReturn(50L);

        assertThrows(RuntimeException.class, () -> messageService.sendMessage(request, "sender"));
    }
}
