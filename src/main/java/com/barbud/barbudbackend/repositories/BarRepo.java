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
    public String createBarWithIngredients(Long userId, String barName, List<Long> ingredientIds) {

        Long barId = jdbcTemplate.queryForObject(
                """
                INSERT INTO bars (user_id, name)
                VALUES (?, ?)
                RETURNING id
                """,
                Long.class,
                userId,
                barName
        );

        addIngredientsToBar(barId, ingredientIds);

        return "Bar added.";
    }

    public List<BarResponse> getAllBarsByUserId(Long userId) {
        return jdbcTemplate.query(
                """
                SELECT 
                    b.id,
                    b.name,
                    COUNT(bi.ingredient_id) AS amount_ingredients
                FROM bars b
                LEFT JOIN bar_ingredients bi
                    ON b.id = bi.bar_id
                WHERE b.user_id = ?
                GROUP BY b.id, b.name
                ORDER BY b.id
                """,
                (rs, rowNum) -> new BarResponse(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("amount_ingredients")
                ),
                userId
        );
    }

    public BarDetailsResponse getBarDetails(Long userId, Long barId) {

        BarResponse bar = jdbcTemplate.queryForObject(
                """
                SELECT 
                    b.id,
                    b.name,
                    COUNT(bi.ingredient_id) AS amount_ingredients
                FROM bars b
                LEFT JOIN bar_ingredients bi
                    ON b.id = bi.bar_id
                WHERE b.id = ?
                AND b.user_id = ?
                GROUP BY b.id, b.name
                """,
                (rs, rowNum) -> new BarResponse(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("amount_ingredients")
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
                "Bar details.",
                bar.getId(),
                bar.getName(),
                ingredients
        );
    }

    @Transactional
    public String updateBar(Long userId, Long barId, String newName, List<Long> ingredientIds) {

        jdbcTemplate.update(
                """
                UPDATE bars
                SET name = ?
                WHERE id = ?
                AND user_id = ?
                """,
                newName,
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

        return "Bar Updated.";
    }

    private void addIngredientsToBar(Long barId, List<Long> ingredientIds) {

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
