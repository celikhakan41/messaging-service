package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final long EXPIRATION_MS = 86400000; // 1 day

    // Sabit bir key kullanım (geliştirme için)
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret:mySecretKeyForDevelopmentThatIsAtLeast32Bytes}") String secret) {
        // En az 256 bit (32 byte) olmalı
        if (secret.length() < 32) {
            secret = "mySecretKeyForDevelopmentThatIsAtLeast32BytesLong123456789";
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        logger.info("JwtService initialized with key");
    }

    public String generateToken(String username, String tenantId, PlanType planType) {
        String token = Jwts.builder()
                .setSubject(username)
                .claim("tenantId", tenantId)
                .claim("planType", planType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();

        logger.debug("Generated JWT token for user: {}", username);
        return token;
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            logger.debug("JWT token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractTenantId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("tenantId", String.class);
        } catch (JwtException e) {
            logger.warn("Failed to extract tenantId from token: {}", e.getMessage());
            throw e;
        }
    }

    public String extractPlanType(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("planType", String.class);
        } catch (JwtException e) {
            logger.warn("Failed to extract planType from token: {}", e.getMessage());
            throw e;
        }
    }
}