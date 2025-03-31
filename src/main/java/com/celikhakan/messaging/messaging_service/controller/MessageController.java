package com.celikhakan.messaging.messaging_service.controller;

import com.celikhakan.messaging.messaging_service.dto.MessageResponse;
import com.celikhakan.messaging.messaging_service.dto.SendMessageRequest;
import com.celikhakan.messaging.messaging_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest request, Principal principal) {
        MessageResponse response = messageService.sendMessage(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<MessageResponse>> getMessages(@RequestParam("with") String withUser, Principal principal) {
        List<MessageResponse> history = messageService.getConversation(withUser, principal.getName());
        return ResponseEntity.ok(history);
    }
}
