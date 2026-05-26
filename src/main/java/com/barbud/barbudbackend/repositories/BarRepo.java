package com.barbud.barbudbackend.repositories;

import com.barbud.barbudbackend.interfaces.IBarRepo;
import com.barbud.barbudbackend.responses.BarDetailsResponse;
import com.barbud.barbudbackend.responses.BarIngredientsResponse;
import com.barbud.barbudbackend.responses.BarResponse;
import com.barbud.barbudbackend.responses.Ingredients;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    public boolean barNameExistsForUser(Long userId, String barName) {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                SELECT EXISTS (
                    SELECT 1
                    FROM bars
                    WHERE user_id = ?
                    AND LOWER(name) = LOWER(?)
                )
                """,
                Boolean.class,
                userId,
                barName
        );

        return Boolean.TRUE.equals(exists);
    }

    public boolean barNameExistsForUserExceptCurrentBar(Long userId, Long barId, String barName) {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                SELECT EXISTS (
                    SELECT 1
                    FROM bars
                    WHERE user_id = ?
                    AND id <> ?
                    AND LOWER(name) = LOWER(?)
                )
                """,
                Boolean.class,
                userId,
                barId,
                barName
        );

        return Boolean.TRUE.equals(exists);
    }

    public boolean barBelongsToUser(Long userId, Long barId) {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                SELECT EXISTS (
                    SELECT 1
                    FROM bars
                    WHERE id = ?
                    AND user_id = ?
                )
                """,
                Boolean.class,
                barId,
                userId
        );

        return Boolean.TRUE.equals(exists);
    }

    @Transactional
    public Long createBarWithIngredients(Long userId, String barName, List<Long> ingredientIds) {
        String cleanedBarName = barName.trim();

        if (barNameExistsForUser(userId, cleanedBarName)) {
            throw new IllegalArgumentException("You already have a bar with this name");
        }

        Long barId = jdbcTemplate.queryForObject(
                """
                INSERT INTO bars (user_id, name)
                VALUES (?, ?)
                RETURNING id
                """,
                Long.class,
                userId,
                cleanedBarName
        );

        addIngredientsToBar(barId, ingredientIds);

        return barId;
    }

    public List<BarResponse> getAllBarsByUserId(Long userId) {
        return jdbcTemplate.query(
                """
                SELECT id, name
                FROM bars
                WHERE user_id = ?
                ORDER BY id
                """,
                (rs, rowNum) -> new BarResponse(
                        rs.getLong("id"),
                        rs.getString("name")
                ),
                userId
        );
    }

    public BarDetailsResponse getBarDetails(Long userId, Long barId) {
        if (!barBelongsToUser(userId, barId)) {
            throw new IllegalArgumentException("Bar not found");
        }

        BarResponse bar = jdbcTemplate.queryForObject(
                """
                SELECT id, name
                FROM bars
                WHERE id = ?
                AND user_id = ?
                """,
                (rs, rowNum) -> new BarResponse(
                        rs.getLong("id"),
                        rs.getString("name")
                ),
                barId,
                userId
        );

        List<BarIngredientsResponse> ingredients = jdbcTemplate.query(
                """
                SELECT i.id, i.name, i.category
                FROM ingredients i
                INNER JOIN bar_ingredients bi
                    ON i.id = bi.ingredient_id
                WHERE bi.bar_id = ?
                ORDER BY i.name
                """,
                (rs, rowNum) -> new BarIngredientsResponse(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("category")
                ),
                barId
        );

        return new BarDetailsResponse(
                bar.getId(),
                bar.getName(),
                ingredients
        );
    }

    @Transactional
    public void updateBar(Long userId, Long barId, String newName, List<Long> ingredientIds) {
        if (!barBelongsToUser(userId, barId)) {
            throw new IllegalArgumentException("Bar not found");
        }

        String cleanedName = newName.trim();

        if (barNameExistsForUserExceptCurrentBar(userId, barId, cleanedName)) {
            throw new IllegalArgumentException("You already have a bar with this name");
        }

        jdbcTemplate.update(
                """
                UPDATE bars
                SET name = ?
                WHERE id = ?
                AND user_id = ?
                """,
                cleanedName,
                barId,
                userId
        );

        jdbcTemplate.update(
                """
                DELETE FROM bar_ingredients
                WHERE bar_id = ?
                """,
                barId
        );

        addIngredientsToBar(barId, ingredientIds);
    }

    private void addIngredientsToBar(Long barId, List<Long> ingredientIds) {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            return;
        }

        List<Long> uniqueIngredientIds = ingredientIds.stream()
                .distinct()
                .toList();

        for (Long ingredientId : uniqueIngredientIds) {
            jdbcTemplate.update(
                    """
                    INSERT INTO bar_ingredients (bar_id, ingredient_id)
                    VALUES (?, ?)
                    ON CONFLICT (bar_id, ingredient_id) DO NOTHING
                    """,
                    barId,
                    ingredientId
            );
        }
    }
}
