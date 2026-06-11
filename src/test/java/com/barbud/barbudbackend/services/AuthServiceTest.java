package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.requests.LoginRequest;
import com.barbud.barbudbackend.requests.RefreshRequest;
import com.barbud.barbudbackend.responses.LoginResponse;
import com.barbud.barbudbackend.interfaces.IAuthRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
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

        assertEquals("You're logged in", response.getMessage());
        assertEquals(1, response.getUserId());
        assertEquals("Wojtek", response.getUsername());
        assertEquals("fake-access-token", response.getAccessToken());
        assertEquals("fake-refresh-token", response.getRefreshToken());

        assertNotNull(response.getAccessExpiresIn());
        assertNotNull(response.getRefreshExpiresIn());
    }

    @Test
    void login_WithIncorrectCredentials_ReturnsInvalidLoginResponse() {
        // Arrange
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "WrongPassword123!"
        );

        String hashedPassword = "hashed-password-from-db";

        when(authRepo.passwordLookup("test@example.com"))
                .thenReturn(Optional.of(hashedPassword));

        when(passwordEncoder.matches("WrongPassword123!", hashedPassword))
                .thenReturn(false);

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertNotNull(response);

        assertEquals("email or password invalid", response.getMessage());
        assertEquals(0, response.getUserId());
        assertNull(response.getUsername());
        assertNull(response.getAccessToken());
        assertNull(response.getAccessExpiresIn());
        assertNull(response.getRefreshToken());
        assertNull(response.getRefreshExpiresIn());
    }

    @Test
    void refresh_WithValidRefreshToken_ReturnsNewLoginResponse() {
        // Arrange
        RefreshRequest request = new RefreshRequest("old-refresh-token");

        when(jwtService.extractEmail("old-refresh-token"))
                .thenReturn("test@example.com");

        when(jwtService.extractUserId("old-refresh-token"))
                .thenReturn(1);

        when(authRepo.usernameLookup("test@example.com"))
                .thenReturn(Optional.of("Wojtek"));

        when(authRepo.refreshTokenLookup("test@example.com"))
                .thenReturn(Optional.of("old-refresh-token"));

        when(authRepo.refreshTokenExpiryLookup("test@example.com"))
                .thenReturn(Optional.of(LocalDateTime.now().plusDays(5)));

        when(jwtService.generateAccessToken(1, "test@example.com"))
                .thenReturn("new-access-token");

        when(jwtService.generateRefreshToken(1, "test@example.com"))
                .thenReturn("new-refresh-token");

        // Act
        LoginResponse response = authService.refresh(request);

        // Assert
        assertNotNull(response);

        assertEquals("Tokens refreshed", response.getMessage());
        assertEquals(1, response.getUserId());
        assertEquals("Wojtek", response.getUsername());
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());

        assertNotNull(response.getAccessExpiresIn());
        assertNotNull(response.getRefreshExpiresIn());
    }

    @Test
    void refresh_WithExpiredRefreshToken_ReturnsExpiredResponse() {
        // Arrange
        RefreshRequest request = new RefreshRequest("expired-refresh-token");

        when(jwtService.extractEmail("expired-refresh-token"))
                .thenReturn("test@example.com");

        when(jwtService.extractUserId("expired-refresh-token"))
                .thenReturn(1);

        when(authRepo.usernameLookup("test@example.com"))
                .thenReturn(Optional.of("Wojtek"));

        when(authRepo.refreshTokenLookup("test@example.com"))
                .thenReturn(Optional.of("expired-refresh-token"));

        when(authRepo.refreshTokenExpiryLookup("test@example.com"))
                .thenReturn(Optional.of(LocalDateTime.now().minusDays(1)));

        // Act
        LoginResponse response = authService.refresh(request);

        // Assert
        assertNotNull(response);

        assertEquals("Refresh token expitred", response.getMessage());
        assertEquals(0, response.getUserId());
        assertNull(response.getUsername());
        assertNull(response.getAccessToken());
        assertNull(response.getAccessExpiresIn());
        assertNull(response.getRefreshToken());
        assertNull(response.getRefreshExpiresIn());
    }
}