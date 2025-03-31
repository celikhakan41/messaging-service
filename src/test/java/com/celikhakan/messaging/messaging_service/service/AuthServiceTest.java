package com.celikhakan.messaging.messaging_service.service;

import com.celikhakan.messaging.messaging_service.dto.AuthResponse;
import com.celikhakan.messaging.messaging_service.dto.LoginRequest;
import com.celikhakan.messaging.messaging_service.dto.RegisterRequest;
import com.celikhakan.messaging.messaging_service.model.User;
import com.celikhakan.messaging.messaging_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("hakan");
        request.setPassword("1234");

        when(userRepository.existsByUsername("hakan")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encoded-password");
        when(jwtService.generateToken("hakan")).thenReturn("mock-jwt");

        AuthResponse response = authService.register(request);

        assertEquals("mock-jwt", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setPassword("pass");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("secret");

        User user = User.builder()
                .username("user")
                .password("encoded-secret")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded-secret")).thenReturn(true);
        when(jwtService.generateToken("user")).thenReturn("mock-jwt");

        AuthResponse response = authService.login(request);
        assertEquals("mock-jwt", response.getToken());
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("wrong");

        User user = User.builder()
                .username("user")
                .password("encoded-password")
                .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}
