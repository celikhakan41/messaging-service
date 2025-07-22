package com.celikhakan.messaging.messaging_service.filter;

import com.celikhakan.messaging.messaging_service.config.AuthDetails;
import com.celikhakan.messaging.messaging_service.model.ApiKey;
import com.celikhakan.messaging.messaging_service.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    private static final String API_KEY_HEADER_NAME = "X-API-KEY";

    private final ApiKeyService apiKeyService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // If already authenticated, skip API Key check.
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            logger.debug("Existing authentication found. Skipping API Key check.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract API Key from header, handling null or blank values.
        final Optional<String> apiKeyValueOptional = Optional.ofNullable(request.getHeader(API_KEY_HEADER_NAME))
                .filter(key -> !key.isBlank());

        if (apiKeyValueOptional.isPresent()) {
            final String apiKeyValue = apiKeyValueOptional.get();
            try {
                ApiKey apiKey = apiKeyService.validateApiKey(apiKeyValue);
                UserDetails userDetails = userDetailsService.loadUserByUsername(apiKey.getUsername());

                var authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Assuming AuthDetails is a record or an immutable class
                var authDetails = new AuthDetails(apiKey.getTenantId(), null, "API_KEY");
                authenticationToken.setDetails(authDetails);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.debug("API Key authentication successful for user: {} (Tenant ID: {})", apiKey.getUsername(), apiKey.getTenantId());

            } catch (Exception ex) {
                logger.warn("API Key authentication failed for value starting with '{}...': {}",
                        apiKeyValue.substring(0, Math.min(apiKeyValue.length(), 10)),
                        ex.getMessage(),
                        ex);

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or unauthorized API Key.");
                return;
            }
        } else {
            logger.debug("No '{}' header or empty value found in the request.", API_KEY_HEADER_NAME);
        }

        filterChain.doFilter(request, response);
    }
}