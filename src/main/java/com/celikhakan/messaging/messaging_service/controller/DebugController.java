package com.celikhakan.messaging.messaging_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return ResponseEntity.ok(Map.of("message", "No authentication found"));
        }

        return ResponseEntity.ok(Map.of(
                "authenticated", auth.isAuthenticated(),
                "principal", auth.getPrincipal(),
                "authorities", auth.getAuthorities(),
                "details", auth.getDetails(),
                "name", auth.getName()
        ));
    }
}