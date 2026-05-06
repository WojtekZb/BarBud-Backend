package com.barbud.barbudbackend.repositories;

import com.barbud.barbudbackend.interfaces.IAuthRepo;
import com.barbud.barbudbackend.requests.RegisterRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


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
            return passwordHash;
        } catch (EmptyResultDataAccessException e) {
            return null;
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
    public String register(RegisterRequest request){
        String sql = """
            SELECT id
            FROM users
            WHERE LOWER(email) = LOWER(?)
            """;
    }
}
