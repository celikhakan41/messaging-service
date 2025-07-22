package com.celikhakan.messaging.messaging_service.config;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.lang.Nullable;
import java.util.Optional;

/**
 * A utility class for safely retrieving current authentication details from the SecurityContext.
 * Designed to avoid direct null checks at the call site by using Optional.
 */
public final class AuthContext {

    private AuthContext() {
    }

    /**
     * Retrieves the current Authentication object from Spring SecurityContextHolder.
     *
     * @return The Authentication object, or null if no authentication is present.
     */
    @Nullable
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Retrieves the AuthDetails from the current Authentication object, if available.
     * Uses Java 16's pattern matching for instanceof and returns an Optional.
     *
     * @return An Optional containing AuthDetails if present and correct type, otherwise an empty Optional.
     */
    public static Optional<AuthDetails> getAuthDetails() {
        Authentication auth = getAuthentication();
        if (auth != null && auth.getDetails() instanceof AuthDetails details) {
            return Optional.of(details);
        }
        return Optional.empty();
    }

    /**
     * Retrieves the tenant ID from AuthDetails, if available.
     *
     * @return An Optional containing the tenant ID, or an empty Optional if not found.
     */
    public static Optional<String> getTenantId() {
        return getAuthDetails().map(AuthDetails::tenantId);
    }

    /**
     * Retrieves the plan type from AuthDetails, if available.
     *
     * @return An Optional containing the PlanType, or an empty Optional if not found.
     */
    public static Optional<PlanType> getPlanType() {
        return getAuthDetails().map(AuthDetails::planType);
    }

    /**
     * Retrieves the authentication type from AuthDetails, if available.
     *
     * @return An Optional containing the authentication type, or an empty Optional if not found.
     */
    public static Optional<String> getAuthType() {
        return getAuthDetails().map(AuthDetails::authType);
    }
}