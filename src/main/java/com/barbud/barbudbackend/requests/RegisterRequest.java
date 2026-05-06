package com.barbud.barbudbackend.requests;

public class RegisterRequest {
    public String email;
    public String hashedPassword;
    public String refreshToken;
    public int refresh_expiresIn;
}
