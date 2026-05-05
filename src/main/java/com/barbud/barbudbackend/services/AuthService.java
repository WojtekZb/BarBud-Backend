package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.requests.LoginRequest;
import com.barbud.barbudbackend.responses.LoginResponse;

public class AuthService {

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

    }
}
