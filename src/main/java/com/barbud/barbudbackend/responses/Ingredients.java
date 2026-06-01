package com.barbud.barbudbackend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Ingredients {
    private int id;
    private String name;
    private String category;
}
