package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.interfaces.IAuthRepo;
import com.barbud.barbudbackend.requests.LoginRequest;
import com.barbud.barbudbackend.responses.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final IAuthRepo AuthRepo;

    public AuthService(PasswordEncoder passwordEncoder, IAuthRepo AuthRepo) {
        this.passwordEncoder = passwordEncoder;
        this.AuthRepo = AuthRepo;
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
        String userPass = AuthRepo.passwordLookup(request.email).orElse(null);
        if (passwordEncoder.matches(request.password, userPass)){
            int userId = AuthRepo.userIdLookup(request.email);
            //JWT here
        }
    }

    public LoginResponse Register(LoginRequest request){
        if (passwordValidation(request.password)){
            String hashPass = passwordEncoder.encode(request.password);
            if (AuthRepo.register(request.email, hashPass)){
                return Login(request);
            }
        }
        else {
            return null;
        }
    }
}
