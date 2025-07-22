package com.celikhakan.messaging.messaging_service.model;

/**
 * Application roles (no prefix). Prefix (ROLE_) is added in UserDetails.getAuthorities().
 */
public enum Role {
    USER,
    ADMIN;
}
