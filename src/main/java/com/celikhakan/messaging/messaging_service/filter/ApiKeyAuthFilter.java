package com.celikhakan.messaging.messaging_service.filter;

import com.celikhakan.messaging.messaging_service.model.ApiKey;
import com.celikhakan.messaging.messaging_service.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    private static final String HEADER = "X-API-KEY";
    private final ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Eğer zaten authentication varsa (JWT ile), API Key kontrolü yapmayalım
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKeyValue = request.getHeader(HEADER);
        if (apiKeyValue != null && !apiKeyValue.isBlank()) {
            try {
                ApiKey apiKey = apiKeyService.validateApiKey(apiKeyValue);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                apiKey.getUsername(), null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                // Tenant ID ve diğer bilgileri details'a ekle
                auth.setDetails(Map.of(
                        "tenantId", apiKey.getTenantId(),
                        "authType", "API_KEY"
                ));

                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.debug("API Key authentication successful for user: {}", apiKey.getUsername());

            } catch (RuntimeException ex) {
                logger.warn("Invalid API Key provided: {}", ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}