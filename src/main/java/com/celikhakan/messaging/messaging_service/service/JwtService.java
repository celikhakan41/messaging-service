package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final long EXPIRATION_TIME_MS = TimeUnit.DAYS.toMillis(1); // 1 day

    // Define constants for claim names to avoid magic strings
    private static final String TENANT_ID_CLAIM = "tenantId";
    private static final String PLAN_TYPE_CLAIM = "planType";

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.isBlank() || secret.length() < 32) {
            logger.warn(
                    "JWT secret key is missing, too short, or invalid. " +
                            "Using a default development key. THIS IS INSECURE FOR PRODUCTION!"
            );
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
        }
        logger.info("JwtService initialized.");
    }

    /**
     * Generates a new JWT token for a given user.
     *
     * @param username The subject of the token, typically the user's identifier.
     * @param tenantId The tenant ID associated with the user.
     * @param planType The plan type of the user.
     * @return A newly generated JWT token string.
     */
    public String generateToken(String username, String tenantId, PlanType planType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME_MS);

        String token = Jwts.builder()
                .setSubject(username)
                .claim(TENANT_ID_CLAIM, tenantId)
                .claim(PLAN_TYPE_CLAIM, planType.name())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256) // Explicitly specify algorithm
                .compact();

        logger.debug("Generated JWT token for user: {}", username);
        return token;
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractTenantId(String token) {
        return parseClaims(token).get(TENANT_ID_CLAIM, String.class);
    }

    public String extractPlanType(String token) {
        return parseClaims(token).get(PLAN_TYPE_CLAIM, String.class);
    }

    /**
     * Validates a JWT token's signature and expiration.
     *
     * @param token The JWT token string.
     * @return true if the token is valid (not expired and signature is correct), false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            logger.debug("JWT token validation successful.");
            return true;
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}