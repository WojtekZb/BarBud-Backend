package com.barbud.barbudbackend.interfaces;

import com.barbud.barbudbackend.responses.Ingredients;

import java.util.List;

public interface IBarRepo {
    List<Ingredients> allIngredients();
}
