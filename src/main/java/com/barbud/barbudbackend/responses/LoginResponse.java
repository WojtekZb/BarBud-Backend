package com.barbud.barbudbackend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private Long userId;
    private String username;
    private String accessToken;
    private LocalDateTime accessExpiresIn;
    private String refreshToken;
    private LocalDateTime refreshExpiresIn;
}
