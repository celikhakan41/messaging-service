package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.model.PlanType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final long EXPIRATION_MS = 86400000; // 1 day
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username, String tenantId, PlanType planType) {
        return Jwts.builder()
                .setSubject(username)
                .claim("tenantId", tenantId)
                .claim("planType", planType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractTenantId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("tenantId", String.class);
    }
    public String extractPlanType(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("planType", String.class);
    }
}