package com.barbud.barbudbackend.controllers;

import com.barbud.barbudbackend.requests.BarDetailsRequest;
import com.barbud.barbudbackend.requests.CreateBarRequest;
import com.barbud.barbudbackend.requests.GetBarsRequest;
import com.barbud.barbudbackend.requests.UpdateBarRequest;
import com.barbud.barbudbackend.responses.Ingredients;
import com.barbud.barbudbackend.services.BarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.barbud.barbudbackend.responses.BarResponse;
import com.barbud.barbudbackend.responses.BarDetailsResponse;


import java.util.List;
import java.util.Map;

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

    @PostMapping("/bar/create")
    public ResponseEntity<?> createBar(@RequestBody CreateBarRequest request) {

        Long barId = barService.createBarWithIngredients(
                request.getUserId(),
                request.getName(),
                request.getIngredientIds()
        );

        return ResponseEntity.status(201).body(Map.of(
                "id", barId,
                "message", "Bar created successfully"
        ));
    }

    @PostMapping("/bar/my-bars")
    public ResponseEntity<List<BarResponse>> getMyBars(@RequestBody GetBarsRequest request) {
        List<BarResponse> bars = barService.getAllBarsByUserId(
                request.getUserId()
        );

        return ResponseEntity.ok(bars);
    }

    @PostMapping("/bar/details")
    public ResponseEntity<BarDetailsResponse> getBarDetails(@RequestBody BarDetailsRequest request) {
        BarDetailsResponse bar = barService.getBarDetails(
                request.getUserId(),
                request.getBarId()
        );

        return ResponseEntity.ok(bar);
    }

    @PutMapping("/bar/update")
    public ResponseEntity<?> updateBar(@RequestBody UpdateBarRequest request) {
        barService.updateBar(
                request.getUserId(),
                request.getBarId(),
                request.getName(),
                request.getIngredientIds()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Bar updated successfully"
        ));
    }
}
