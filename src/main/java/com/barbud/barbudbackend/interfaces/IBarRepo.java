package com.barbud.barbudbackend.interfaces;

import com.barbud.barbudbackend.responses.BarDetailsResponse;
import com.barbud.barbudbackend.responses.BarResponse;
import com.barbud.barbudbackend.responses.Ingredients;
import org.springframework.context.annotation.Bean;

import java.util.List;


public interface IBarRepo {
    List<Ingredients> allIngredients();
    Long createBarWithIngredients(Long userId, String barName, List<Long> ingredientIds);
    List<BarResponse> getAllBarsByUserId(Long userId);
    BarDetailsResponse getBarDetails(Long userId, Long barId);
    void updateBar(Long userId, Long barId, String newName, List<Long> ingredientIds);
}
