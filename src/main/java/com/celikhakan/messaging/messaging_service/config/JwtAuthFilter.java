package com.celikhakan.messaging.messaging_service.config;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import com.celikhakan.messaging.messaging_service.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        logger.debug("JwtAuthFilter processing request: {} {}", request.getMethod(), request.getRequestURI());

        // Skip JWT authentication if already authenticated (e.g., by ApiKeyAuthFilter).
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            logger.debug("Authentication already exists, skipping JWT filter.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract Authorization header and check for Bearer token.
        final Optional<String> jwtTokenOptional = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX_LENGTH));

        if (jwtTokenOptional.isPresent()) {
            final String token = jwtTokenOptional.get();
            logger.debug("Extracted JWT token (first 20 chars): {}...", token.substring(0, Math.min(20, token.length())));

            try {
                if (jwtService.validateToken(token)) {
                    String username = jwtService.extractUsername(token);
                    String tenantId = jwtService.extractTenantId(token);
                    PlanType planType = PlanType.valueOf(jwtService.extractPlanType(token));

                    logger.debug("JWT validation successful for user: {}, tenantId: {}, planType: {}", username, tenantId, planType);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credentials are null as token is already validated
                            userDetails.getAuthorities()
                    );

                    // AuthDetails is a record, so direct instantiation is concise
                    var authDetails = new AuthDetails(tenantId, planType, "JWT");
                    authenticationToken.setDetails(authDetails);

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.debug("JWT authentication set for user: {}", username);
                } else {
                    logger.warn("JWT token validation failed for unknown reason (validateToken returned false).");
                }
            } catch (Exception e) {
                // Catch all exceptions during token processing (e.g., ExpiredJwtException, MalformedJwtException, etc.)
                logger.warn("JWT token processing failed: {}", e.getMessage(), e); // Log full exception for debugging
                // Do NOT send error response here. Let Spring Security's exception handling (e.g., AuthenticationEntryPoint) manage it.
            }
        } else {
            logger.debug("No '{}' header or valid '{}' token found.", AUTHORIZATION_HEADER, BEARER_PREFIX.trim());
        }

        filterChain.doFilter(request, response);
    }
}
