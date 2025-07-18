package com.celikhakan.messaging.messaging_service.config;

import com.celikhakan.messaging.messaging_service.service.JwtService;
import com.celikhakan.messaging.messaging_service.service.ApiKeyService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Handshake interceptor supporting both JWT and API Key authentication for WebSocket connections.
 */
/**
 * Handshake interceptor supporting both JWT and API Key authentication for WebSocket connections.
 */
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    private final JwtService jwtService;
    private final ApiKeyService apiKeyService;

    public WebSocketHandshakeInterceptor(JwtService jwtService, ApiKeyService apiKeyService) {
        this.jwtService = jwtService;
        this.apiKeyService = apiKeyService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler handler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            var httpReq = servletRequest.getServletRequest();

            String token = httpReq.getParameter("token");
            if (token != null && jwtService.validateToken(token)) {
                attributes.put("username", jwtService.extractUsername(token));
                attributes.put("tenantId", jwtService.extractTenantId(token));
                attributes.put("authType", "JWT");
                return true;
            }

            String apiKey = httpReq.getHeader("X-API-KEY");
            if (apiKey != null && !apiKey.isBlank()) {
                try {
                    var key = apiKeyService.validateApiKey(apiKey);
                    attributes.put("username", key.getUsername());
                    attributes.put("tenantId", key.getTenantId());
                    attributes.put("authType", "API_KEY");
                    return true;
                } catch (RuntimeException ex) {
                    logger.warn("Invalid API Key in WebSocket handshake.");
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler handler, Exception ex) {
    }
}
