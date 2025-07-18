package com.celikhakan.messaging.messaging_service.model;


public enum PlanType {
    FREE(50),
    PRO(1000),
    ENTERPRISE(Integer.MAX_VALUE);

    private final int dailyMessageLimit;

    PlanType(int dailyMessageLimit) {
        this.dailyMessageLimit = dailyMessageLimit;
    }

    public int getDailyMessageLimit() {
        return dailyMessageLimit;
    }
}