package com.barbud.barbudbackend.controllers;
//TODO refresh, login, register

import com.barbud.barbudbackend.requests.LoginRequest;
import com.barbud.barbudbackend.requests.RefreshRequest;
import com.barbud.barbudbackend.requests.RegisterRequest;
import com.barbud.barbudbackend.responses.LoginResponse;
import com.barbud.barbudbackend.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("auth/refresh")
    public LoginResponse Refresh(@RequestBody RefreshRequest request){
        return authService.Refresh(request);
    }

    @PostMapping("auth/login")
    public LoginResponse Login(@RequestBody LoginRequest request){
        LoginResponse response = authService.Login(request);
        return response;
    }

    @PostMapping("/auth/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        String step1 = authService.Register(request);

        if ("User added".equals(step1)) {
            LoginRequest loginRequest = new LoginRequest(
                    request.email,
                    request.password
            );
            return authService.Login(loginRequest);
        }

        return null;
    }
}
