package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.requests.LoginRequest;
import com.barbud.barbudbackend.responses.LoginResponse;
import com.barbud.barbudbackend.interfaces.IAuthRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private IAuthRepo authRepo;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authRepo = mock(IAuthRepo.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);

        authService = new AuthService(authRepo, passwordEncoder, jwtService);
    }

    @Test
    void login_WithCorrectCredentials_ReturnsLoginResponse() {
        // Arrange
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "Password123!"
        );

        String hashedPassword = "hashed-password-from-db";

        when(authRepo.passwordLookup("test@example.com"))
                .thenReturn(Optional.of(hashedPassword));

        when(passwordEncoder.matches("Password123!", hashedPassword))
                .thenReturn(true);

        when(authRepo.userIdLookup("test@example.com"))
                .thenReturn(1);

        when(authRepo.usernameLookup("test@example.com"))
                .thenReturn(Optional.of("Wojtek"));

        when(jwtService.generateAccessToken(1, "test@example.com"))
                .thenReturn("fake-access-token");

        when(jwtService.generateRefreshToken(1, "test@example.com"))
                .thenReturn("fake-refresh-token");

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertNotNull(response);

        assertEquals(1, response.getUserId());
        assertEquals("Wojtek", response.getUsername());
        assertEquals("fake-access-token", response.getAccessToken());
        assertEquals("fake-refresh-token", response.getRefreshToken());

        assertNotNull(response.getAccessExpiresIn());
        assertNotNull(response.getRefreshExpiresIn());

        verify(authRepo).passwordLookup("test@example.com");
        verify(passwordEncoder).matches("Password123!", hashedPassword);
        verify(authRepo).userIdLookup("test@example.com");
        verify(authRepo).usernameLookup("test@example.com");
        verify(jwtService).generateAccessToken(1, "test@example.com");
        verify(jwtService).generateRefreshToken(1, "test@example.com");

        verify(authRepo).saveRefreshToken(
                eq("test@example.com"),
                eq("fake-refresh-token"),
                any()
        );
    }
}