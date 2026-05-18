package com.barbud.barbudbackend.interfaces;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IAuthRepo {

    Optional<String> passwordLookup(String email);

    Optional<String> usernameLookup(String email);

    int userIdLookup(String email);

    String register(String email, String username, String passwordHash);

    String saveRefreshToken(String email, String refreshTokenHash, LocalDateTime refreshTokenExpiry);

    Optional<String> refreshTokenLookup(String email);
    Optional<LocalDateTime> refreshTokenExpiryLookup(String email);
}
