package com.celikhakan.messaging.messaging_service.model;


import lombok.Getter;

@Getter
public enum PlanType {
    FREE(50),
    PRO(1000),
    ENTERPRISE(Integer.MAX_VALUE);

    private final int dailyMessageLimit;

    PlanType(int dailyMessageLimit) {
        this.dailyMessageLimit = dailyMessageLimit;
    }

}