package com.celikhakan.messaging.messaging_service.config;

import com.celikhakan.messaging.messaging_service.service.JwtService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public WebSocketHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler handler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");

            if (token != null && jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                String tenantId = jwtService.extractTenantId(token);
                String planType = jwtService.extractPlanType(token);
                attributes.put("username", username);
                attributes.put("tenantId", tenantId);
                attributes.put("planType", planType);
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler handler, Exception ex) {
    }
}