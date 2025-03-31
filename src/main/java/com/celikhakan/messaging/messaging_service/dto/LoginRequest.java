package com.celikhakan.messaging.messaging_service.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
