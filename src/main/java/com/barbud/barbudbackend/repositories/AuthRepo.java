package com.barbud.barbudbackend.repositories;

import com.barbud.barbudbackend.interfaces.IAuthRepo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AuthRepo implements IAuthRepo {

    private final JdbcTemplate jdbcTemplate;

    public AuthRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<String> passwordLookup(String email) {
        String sql = """
            SELECT password_hash
            FROM users
            WHERE LOWER(email) = LOWER(?)
            """;

        try {
            String passwordHash = jdbcTemplate.queryForObject(sql, String.class, email);
            return Optional.ofNullable(passwordHash);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> usernameLookup(String email) {
        String sql = """
            SELECT username
            FROM users
            WHERE LOWER(email) = LOWER(?)
            """;

        try {
            String username = jdbcTemplate.queryForObject(sql, String.class, email);
            return Optional.ofNullable(username);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int userIdLookup(String email) {
        String sql = """
            SELECT id
            FROM users
            WHERE LOWER(email) = LOWER(?)
            """;

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, email);
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    @Override
    public String register(String email,String username, String passwordHash) {
        String sql = """
            INSERT INTO users (email, username, password_hash)
            VALUES (?, ?, ?)
            """;

        try {
            int rowsAffected = jdbcTemplate.update(sql, email, username, passwordHash);

            if (rowsAffected > 0) {
                return "User added";
            }

            return "User could not be added";

        } catch (DuplicateKeyException e) {
            return "User already exists";
        }
    }

    @Override
    public String saveRefreshToken(String email, String refreshTokenHash, LocalDateTime refreshTokenExpiry) {
        String sql = """
            UPDATE users
            SET refresh_token = ?,
                refresh_token_expiry = ?
            WHERE LOWER(email) = LOWER(?)
            """;

        int rowsAffected = jdbcTemplate.update(
                sql,
                refreshTokenHash,
                refreshTokenExpiry,
                email
        );

        if (rowsAffected > 0) {
            return "Refresh token saved";
        }

        return "User not found";
    }

    @Override
    public Optional<String> refreshTokenLookup(String email) {
        String sql = """
            SELECT refresh_token
            FROM users
            WHERE LOWER(email) = LOWER(?)
            """;

        try {
            String refreshToken = jdbcTemplate.queryForObject(sql, String.class, email);
            return Optional.ofNullable(refreshToken);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<LocalDateTime> refreshTokenExpiryLookup(String email) {
        String sql = """
            SELECT refresh_token_expiry
            FROM users
            WHERE LOWER(email) = LOWER(?)
            """;

        try {
            LocalDateTime refreshTokenExpiry = jdbcTemplate.queryForObject(sql, LocalDateTime.class, email);
            return Optional.ofNullable(refreshTokenExpiry);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
