package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.interfaces.IAuthRepo;
import com.barbud.barbudbackend.requests.LoginRequest;
import com.barbud.barbudbackend.requests.RefreshRequest;
import com.barbud.barbudbackend.requests.RegisterRequest;
import com.barbud.barbudbackend.responses.LoginResponse;
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

    public LoginResponse Login(LoginRequest request){
        String userPass = authRepo.passwordLookup(request.getEmail()).orElse(null);

        if (passwordEncoder.matches(request.getPassword(), userPass)){
            int userId = authRepo.userIdLookup(request.getEmail());

            String accessToken = jwtService.generateAccessToken(userId, request.getEmail());
            String refreshToken = jwtService.generateRefreshToken(userId, request.getEmail());

            LocalDateTime refreshTokenExpiry = LocalDateTime.now().plusDays(30);
            LocalDateTime accessTokenExpiry = LocalDateTime.now().plusHours(1);

            authRepo.saveRefreshToken(request.getEmail(), refreshToken, refreshTokenExpiry);

            return new LoginResponse(
                    userId,
                    accessToken,
                    accessTokenExpiry,
                    refreshToken,
                    refreshTokenExpiry
            );
        }
        else return null;
    }

    public String Register(RegisterRequest request){
        if (passwordValidation(request.password)) {
            String hashPass = passwordEncoder.encode(request.password);
            return authRepo.register(request.email, request.username, hashPass);
        }
        else return "User couldnt be added.";
    }

    public LoginResponse Refresh(RefreshRequest request){

        String email = jwtService.extractEmail(request.refreshToken);
        Integer userId = jwtService.extractUserId(request.refreshToken);

        if (email == null || userId == null) {
            return null;
        }

        String savedRefreshToken = authRepo.refreshTokenLookup(email).orElse(null);

        if (savedRefreshToken == null) {
            return null;
        }

        LocalDateTime savedRefreshTokenExpiry = authRepo.refreshTokenExpiryLookup(email).orElse(null);

        if (savedRefreshTokenExpiry == null || savedRefreshTokenExpiry.isBefore(LocalDateTime.now())) {
            return null;
        }

        if (!request.refreshToken.equals(savedRefreshToken)) {
            return null;
        }

        String AccessToken = jwtService.generateAccessToken(userId, email);
        LocalDateTime accessExpiry = LocalDateTime.now().plusHours(1);
        String RefreshToken = jwtService.generateRefreshToken(userId, email);
        LocalDateTime RefreshExpiry = LocalDateTime.now().plusDays(30);

        authRepo.saveRefreshToken(email, RefreshToken, RefreshExpiry);

        return new LoginResponse(
                userId,
                AccessToken,
                accessExpiry,
                RefreshToken,
                RefreshExpiry
        );
    }
}
