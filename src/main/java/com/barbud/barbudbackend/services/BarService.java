package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.interfaces.IBarRepo;
import com.barbud.barbudbackend.responses.Ingredients;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarService {

    private final IBarRepo barRepo;

    public BarService(IBarRepo barRepo) {
        this.barRepo = barRepo;
    }

    public List<Ingredients> GetAllIngredients() {
        return barRepo.allIngredients();
    }
}
