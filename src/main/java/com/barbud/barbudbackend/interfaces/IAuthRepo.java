package com.barbud.barbudbackend.interfaces;

import java.util.Optional;

public interface IAuthRepo {
    Optional<String> passwordLookup(String email);
    int userIdLookup(String email);
}
