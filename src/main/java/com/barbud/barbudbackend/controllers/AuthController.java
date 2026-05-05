package com.barbud.barbudbackend.controllers;
//TODO refresh, login, register

import com.barbud.barbudbackend.requests.LoginRequest;
import com.barbud.barbudbackend.responses.LoginResponse;
import com.barbud.barbudbackend.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("Auth/Login")
    public LoginResponse Login(LoginRequest request){
        LoginResponse response = AuthService.Login(request);
        return response;
    }
}
