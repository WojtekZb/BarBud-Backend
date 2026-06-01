package com.barbud.barbudbackend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String username;
    private String password;
}
