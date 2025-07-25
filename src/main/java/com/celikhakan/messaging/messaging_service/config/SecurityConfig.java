package com.celikhakan.messaging.messaging_service.config;

import com.celikhakan.messaging.messaging_service.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.celikhakan.messaging.messaging_service.filter.ApiKeyAuthFilter;
import com.celikhakan.messaging.messaging_service.filter.RateLimitFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ApiKeyAuthFilter apiKeyAuthFilter;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          ApiKeyAuthFilter apiKeyAuthFilter,
                          RateLimitFilter rateLimitFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.apiKeyAuthFilter = apiKeyAuthFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/debug/**", "/ws/**", "/ws/info/**").permitAll()
                        .anyRequest().authenticated()
                )
                // JWT filter'ı önce çalıştır, sonra API Key filter'ı
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(apiKeyAuthFilter, JwtAuthFilter.class)
                .addFilterAfter(rateLimitFilter, ApiKeyAuthFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
