package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.interfaces.IAuthRepo;
import com.barbud.barbudbackend.requests.LoginRequest;
import com.barbud.barbudbackend.requests.RefreshRequest;
import com.barbud.barbudbackend.requests.RegisterRequest;
import com.barbud.barbudbackend.responses.LoginResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final IAuthRepo authRepo;
    private final JwtService jwtService;

    public AuthService(IAuthRepo authRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authRepo = authRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private boolean passwordValidation(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasCapital && hasNumber && hasSpecial;
    }

    public LoginResponse login(LoginRequest request){
        String userPass = authRepo.passwordLookup(request.getEmail()).orElse(null);

        if (passwordEncoder.matches(request.getPassword(), userPass)){
            int userId = authRepo.userIdLookup(request.getEmail());
            String username = authRepo.usernameLookup(request.getEmail()).orElse(null);

            String accessToken = jwtService.generateAccessToken(userId, request.getEmail());
            String refreshToken = jwtService.generateRefreshToken(userId, request.getEmail());

            LocalDateTime refreshTokenExpiry = LocalDateTime.now().plusDays(30);
            LocalDateTime accessTokenExpiry = LocalDateTime.now().plusHours(1);

            authRepo.saveRefreshToken(request.getEmail(), refreshToken, refreshTokenExpiry);

            return new LoginResponse(
                    "You're logged in",
                    userId,
                    username,
                    accessToken,
                    accessTokenExpiry,
                    refreshToken,
                    refreshTokenExpiry
            );
        }
        else return new LoginResponse(
                "email or password invalid",
                0,
                null,
                null,
                null,
                null,
                null
        );
    }

    public String register(RegisterRequest request){
        if (passwordValidation(request.getPassword())) {
            String hashPass = passwordEncoder.encode(request.getPassword());
            return authRepo.register(request.getEmail(), request.getUsername(), hashPass);
        }
        else return "User couldnt be added.";
    }

    public LoginResponse refresh(RefreshRequest request){

        String email = jwtService.extractEmail(request.getRefreshToken());
        Integer userId = jwtService.extractUserId(request.getRefreshToken());
        String username = authRepo.usernameLookup(email).orElse(null);

        if (email == null || userId == null) {
            return new LoginResponse(
                    "email or password invalid",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        String savedRefreshToken = authRepo.refreshTokenLookup(email).orElse(null);

        if (savedRefreshToken == null) {
            return new LoginResponse(
                    "User has no saved token",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        LocalDateTime savedRefreshTokenExpiry = authRepo.refreshTokenExpiryLookup(email).orElse(null);

        if (savedRefreshTokenExpiry == null || savedRefreshTokenExpiry.isBefore(LocalDateTime.now())) {
            return new LoginResponse(
                    "User has no saved token",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        if (!request.getRefreshToken().equals(savedRefreshToken)) {
            return new LoginResponse(
                    "Tokens don't match",
                    0,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        String AccessToken = jwtService.generateAccessToken(userId, email);
        LocalDateTime accessExpiry = LocalDateTime.now().plusHours(1);
        String RefreshToken = jwtService.generateRefreshToken(userId, email);
        LocalDateTime RefreshExpiry = LocalDateTime.now().plusDays(30);

        authRepo.saveRefreshToken(email, RefreshToken, RefreshExpiry);

        return new LoginResponse(
                "Tokens refreshed",
                userId,
                username,
                AccessToken,
                accessExpiry,
                RefreshToken,
                RefreshExpiry
        );
    }
}
