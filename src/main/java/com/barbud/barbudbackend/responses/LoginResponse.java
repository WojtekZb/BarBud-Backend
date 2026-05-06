package com.barbud.barbudbackend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private int userId;
    private String accessToken;
    private LocalDateTime accessExpiresIn;
    private String refreshToken;
    private LocalDateTime refreshExpiresIn;
}
