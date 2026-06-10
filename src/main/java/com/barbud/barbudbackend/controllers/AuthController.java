package com.barbud.barbudbackend.controllers;

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
    public LoginResponse refresh(@RequestBody RefreshRequest request){
        return authService.refresh(request);
    }

    @PostMapping("auth/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/auth/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        String step1 = authService.register(request);

        if (!"User added".equals(step1)) {
            return new LoginResponse(
                    step1,
                    0,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        LoginRequest loginRequest = new LoginRequest(
                request.getEmail(),
                request.getPassword()
        );
        return authService.login(loginRequest);
    }
}
