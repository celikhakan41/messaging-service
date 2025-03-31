package com.celikhakan.messaging.messaging_service.integration;

import com.celikhakan.messaging.messaging_service.dto.LoginRequest;
import com.celikhakan.messaging.messaging_service.dto.RegisterRequest;
import com.celikhakan.messaging.messaging_service.dto.SendMessageRequest;
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

    @Test
    @Order(1)
    void shouldRegisterSenderAndReceiver() throws Exception {
        RegisterRequest sender = new RegisterRequest();
        sender.setUsername("sender");
        sender.setPassword("123");

        RegisterRequest receiver = new RegisterRequest();
        receiver.setUsername("receiver");
        receiver.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sender)));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiver)));
    }

    @Test
    @Order(2)
    void shouldLoginAndGetToken() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("sender");
        login.setPassword("123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        token = objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    @Order(3)
    void shouldSendMessage() throws Exception {
        SendMessageRequest request = new SendMessageRequest();
        request.setTo("receiver");
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
        mockMvc.perform(get("/api/messages/history?with=receiver")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].to").value("receiver"));
    }
}