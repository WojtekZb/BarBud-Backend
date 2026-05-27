package com.barbud.barbudbackend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BarIngredientsResponse {
    private Long id;
    private String name;
    private String category;
}
