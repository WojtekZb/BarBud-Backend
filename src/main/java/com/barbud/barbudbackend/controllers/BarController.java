package com.barbud.barbudbackend.controllers;

import com.barbud.barbudbackend.requests.BarDetailsRequest;
import com.barbud.barbudbackend.requests.CreateBarRequest;
import com.barbud.barbudbackend.requests.GetBarsRequest;
import com.barbud.barbudbackend.requests.UpdateBarRequest;
import com.barbud.barbudbackend.responses.Ingredients;
import com.barbud.barbudbackend.responses.MessageResponse;
import com.barbud.barbudbackend.services.BarService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.barbud.barbudbackend.responses.BarResponse;
import com.barbud.barbudbackend.responses.BarDetailsResponse;


import java.util.List;

@RestController
public class BarController {

    private final BarService barService;

    public BarController(BarService barService) {
        this.barService = barService;
    }

    @GetMapping("/bar/ingredients")
    public List<Ingredients> allIngredients() {
        return barService.getAllIngredients();
    }

    @PostMapping("/bar/create")
    public MessageResponse createBar(@Valid @RequestBody CreateBarRequest request) {
        String response = barService.createBarWithIngredients(request);
        return new MessageResponse(response);
    }

    @PostMapping("/bar/my-bars")
    public List<BarResponse> getMyBars(@Valid @RequestBody GetBarsRequest request) {
        return barService.getAllBarsByUserId(request);
    }

    @PostMapping("/bar/details")
    public BarDetailsResponse getBarDetails(@Valid @RequestBody BarDetailsRequest request) {
        return barService.getBarDetails(request);
    }

    @PutMapping("/bar/update")
    public MessageResponse updateBar(@Valid @RequestBody UpdateBarRequest request) {
        String response = barService.updateBar(request);
        return new MessageResponse(response);
    }
}
