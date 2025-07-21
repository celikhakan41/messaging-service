package com.celikhakan.messaging.messaging_service.config;

import com.celikhakan.messaging.messaging_service.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.debug("JwtAuthFilter processing request: {} {}", request.getMethod(), request.getRequestURI());

        // Eğer zaten authentication varsa (ApiKey ile), JWT kontrolü yapmayalım
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.debug("Authentication already exists, skipping JWT filter");
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", header != null ? "Bearer ***" : "null");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            logger.debug("Extracted JWT token (first 20 chars): {}...", token.substring(0, Math.min(20, token.length())));

            try {
                if (jwtService.validateToken(token)) {
                    String username = jwtService.extractUsername(token);
                    String tenantId = jwtService.extractTenantId(token);
                    String planType = jwtService.extractPlanType(token);

                    logger.debug("JWT validation successful for user: {}, tenantId: {}", username, tenantId);

                    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(username)
                            .password("") // şifre önemli değil
                            .authorities("ROLE_USER")
                            .build();

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    auth.setDetails(Map.of(
                            "tenantId", tenantId,
                            "planType", planType
                    ));


                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("JWT authentication successful for user: {}", username);
                } else {
                    logger.warn("JWT token validation failed");
                }
            } catch (Exception e) {
                logger.warn("JWT token validation failed: {}", e.getMessage());
            }
        } else {
            logger.debug("No valid Authorization header found");
        }

        filterChain.doFilter(request, response);
    }
}
