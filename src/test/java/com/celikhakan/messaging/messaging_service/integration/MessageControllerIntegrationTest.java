package com.celikhakan.messaging.messaging_service.integration;

import com.celikhakan.messaging.messaging_service.dto.LoginRequest;
import com.celikhakan.messaging.messaging_service.dto.RegisterRequest;
import com.celikhakan.messaging.messaging_service.dto.SendMessageRequest;
import com.celikhakan.messaging.messaging_service.model.PlanType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static String token;
    static String senderUsername;
    static String receiverUsername;

    @BeforeAll
    static void setupUsernames() {
        senderUsername = "sender_" + System.currentTimeMillis();
        receiverUsername = "receiver_" + System.currentTimeMillis();
    }

    @Test
    @Order(1)
    void shouldRegisterSenderAndReceiver() throws Exception {
        RegisterRequest sender = new RegisterRequest();
        sender.setUsername(senderUsername);
        sender.setPassword("123");
        sender.setPlanType(PlanType.FREE);

        RegisterRequest receiver = new RegisterRequest();
        receiver.setUsername(receiverUsername);
        receiver.setPassword("123");
        receiver.setPlanType(PlanType.FREE);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sender)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiver)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @Order(2)
    void shouldLoginAndGetToken() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername(senderUsername);
        login.setPassword("123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        token = objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    @Order(3)
    void shouldSendMessage() throws Exception {
        SendMessageRequest request = new SendMessageRequest();
        request.setTo(receiverUsername);
        request.setContent("Hello integration test!");

        mockMvc.perform(post("/api/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello integration test!"));
    }

    @Test
    @Order(4)
    void shouldGetMessageHistory() throws Exception {
        mockMvc.perform(get("/api/messages/history?with=" + receiverUsername)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].to").value(receiverUsername));
    }
}