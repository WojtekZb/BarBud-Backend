package com.barbud.barbudbackend.repositories;

import com.barbud.barbudbackend.interfaces.IBarRepo;
import com.barbud.barbudbackend.responses.Ingredients;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BarRepo implements IBarRepo {

    private final JdbcTemplate jdbcTemplate;

    public BarRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ingredients> allIngredients() {
        String sql = """
            SELECT *
            FROM ingredients
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Ingredients(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category")
                )
        );
    }
}
