package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.interfaces.IBarRepo;
import com.barbud.barbudbackend.responses.BarDetailsResponse;
import com.barbud.barbudbackend.responses.Ingredients;
import org.springframework.stereotype.Service;
import com.barbud.barbudbackend.responses.BarResponse;

import java.util.List;

@Service
public class BarService {

    private final IBarRepo barRepo;

    public BarService(IBarRepo barRepo) {
        this.barRepo = barRepo;
    }

    public List<Ingredients> getAllIngredients() {
        return barRepo.allIngredients();
    }

    public Long createBarWithIngredients(Long userId, String barName, List<Long> ingredientIds) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }

        if (barName == null || barName.isBlank()) {
            throw new IllegalArgumentException("Bar name is required");
        }

        return barRepo.createBarWithIngredients(userId, barName.trim(), ingredientIds);
    }

    public List<BarResponse> getAllBarsByUserId(Long userId) {
        validateUserId(userId);

        return barRepo.getAllBarsByUserId(userId);
    }

    public BarDetailsResponse getBarDetails(Long userId, Long barId) {
        validateUserId(userId);
        validateBarId(barId);

        return barRepo.getBarDetails(userId, barId);
    }

    public void updateBar(Long userId, Long barId, String name, List<Long> ingredientIds) {
        validateUserId(userId);
        validateBarId(barId);
        validateBarName(name);

        barRepo.updateBar(userId, barId, name, ingredientIds);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
    }

    private void validateBarId(Long barId) {
        if (barId == null) {
            throw new IllegalArgumentException("Bar id is required");
        }
    }

    private void validateBarName(String barName) {
        if (barName == null || barName.isBlank()) {
            throw new IllegalArgumentException("Bar name is required");
        }
    }
}
