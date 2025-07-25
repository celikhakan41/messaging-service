package com.celikhakan.messaging.messaging_service.filter;

import com.celikhakan.messaging.messaging_service.config.AuthContext;
import com.celikhakan.messaging.messaging_service.config.AuthDetails;
import com.celikhakan.messaging.messaging_service.model.PlanType;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory rate limiting filter per tenant using Bucket4j.
 * Applies rateLimitPerMinute from PlanType for API calls.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        AuthDetails details = AuthContext.getAuthDetails().orElse(null);
        if (details != null) {
            String tenantId = details.tenantId();
            PlanType plan = details.planType();
            Bucket bucket = buckets.computeIfAbsent(tenantId, id -> createBucket(plan));
            if (!bucket.tryConsume(1)) {
                response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Too many requests");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private Bucket createBucket(PlanType plan) {
        Bandwidth limit = Bandwidth.classic(
                plan.getRateLimitPerMinute(),
                Refill.greedy(plan.getRateLimitPerMinute(), Duration.ofMinutes(1))
        );
        return Bucket4j.builder().addLimit(limit).build();
    }
}
