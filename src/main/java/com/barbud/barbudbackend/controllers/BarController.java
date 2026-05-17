package com.barbud.barbudbackend.controllers;

import com.barbud.barbudbackend.responses.Ingredients;
import com.barbud.barbudbackend.services.BarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BarController {

    private final BarService barService;

    public BarController(BarService barService) {
        this.barService = barService;
    }

    @GetMapping("/bar/ingredients")
    public List<Ingredients> AllIngredients() {
        return barService.GetAllIngredients();
    }
}
