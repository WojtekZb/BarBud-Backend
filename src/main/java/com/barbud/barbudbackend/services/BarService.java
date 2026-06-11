package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.interfaces.IBarRepo;
import com.barbud.barbudbackend.requests.BarDetailsRequest;
import com.barbud.barbudbackend.requests.CreateBarRequest;
import com.barbud.barbudbackend.requests.GetBarsRequest;
import com.barbud.barbudbackend.requests.UpdateBarRequest;
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

    public String createBarWithIngredients(CreateBarRequest request) {

        String cleanedName = request.getName().trim();

        if (barRepo.barNameExistsForUser(request.getUserId(), cleanedName)) {
            return "You already have a bar with this name.";
        }

        return barRepo.createBarWithIngredients(request.getUserId(), cleanedName, request.getIngredientIds());
    }

    public List<BarResponse> getAllBarsByUserId(GetBarsRequest request) {
        return barRepo.getAllBarsByUserId(request.getUserId());
    }

    public BarDetailsResponse getBarDetails(BarDetailsRequest request) {

        if (!barRepo.barBelongsToUser(request.getUserId(), request.getBarId())) {
            return new BarDetailsResponse(
                    "Bar not found.",
                    null,
                    null,
                    null
            );
        }

        return barRepo.getBarDetails(request.getUserId(), request.getBarId());
    }

    public String updateBar(UpdateBarRequest request) {

        String cleanedName = request.getName().trim();

        if (!barRepo.barBelongsToUser(request.getUserId(), request.getBarId())) {
            return "Bar not found";
        }

        if (barRepo.barNameExistsForUserExceptCurrentBar(request.getUserId(), request.getBarId(),cleanedName)) {
            return "You already have a bar with this name";
        }

        return barRepo.updateBar(request.getUserId(), request.getBarId(), cleanedName, request.getIngredientIds());
    }
}
