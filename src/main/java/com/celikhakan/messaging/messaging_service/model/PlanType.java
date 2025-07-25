package com.celikhakan.messaging.messaging_service.model;


import lombok.Getter;

    @Getter
public enum PlanType {
    FREE(50, 10),        // 50 msgs/day, 10 req/minute
    PRO(1000, 100),      // 1000 msgs/day, 100 req/minute
    ENTERPRISE(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int dailyMessageLimit;
    private final int rateLimitPerMinute;

    PlanType(int dailyMessageLimit, int rateLimitPerMinute) {
        this.dailyMessageLimit = dailyMessageLimit;
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

}