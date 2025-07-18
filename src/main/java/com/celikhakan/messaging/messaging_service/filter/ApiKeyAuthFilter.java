package com.celikhakan.messaging.messaging_service.filter;

import com.celikhakan.messaging.messaging_service.model.ApiKey;
import com.celikhakan.messaging.messaging_service.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private static final String HEADER = "X-API-KEY";
    private final ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String apiKeyValue = request.getHeader(HEADER);
            if (apiKeyValue != null && !apiKeyValue.isBlank()) {
                try {
                    ApiKey apiKey = apiKeyService.validateApiKey(apiKeyValue);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    apiKey.getUsername(), null,
                                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                    auth.setDetails(apiKey.getTenantId());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (RuntimeException ex) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
