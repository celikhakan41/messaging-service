package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.dto.AuthResponse;
import com.celikhakan.messaging.messaging_service.dto.LoginRequest;
import com.celikhakan.messaging.messaging_service.dto.RegisterRequest;
import com.celikhakan.messaging.messaging_service.model.PlanType;
import com.celikhakan.messaging.messaging_service.model.User;
import com.celikhakan.messaging.messaging_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.celikhakan.messaging.messaging_service.service.TenantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TenantService tenantService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Attempt to register with existing username: {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        PlanType plan = request.getPlanType() == null ? PlanType.FREE : request.getPlanType();
        String tenantId = tenantService.createTenant(plan).getId();
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .tenantId(tenantId)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        logger.info("User registered: {}", user.getUsername());
        String token = jwtService.generateToken(user.getUsername(), user.getTenantId(), plan);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed: user not found: {}", request.getUsername());
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed: incorrect password for user: {}", request.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        PlanType plan = tenantService.getTenant(user.getTenantId()).getPlanType();
        String token = jwtService.generateToken(user.getUsername(), user.getTenantId(), plan);
        return new AuthResponse(token);
    }
}
