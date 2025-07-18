package com.celikhakan.messaging.messaging_service.dto;


import lombok.Data;
import com.celikhakan.messaging.messaging_service.model.PlanType;


@Data
public class RegisterRequest {
    private String username;
    private String password;
    private PlanType planType;
}
