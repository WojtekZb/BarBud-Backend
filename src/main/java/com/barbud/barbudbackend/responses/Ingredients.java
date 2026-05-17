package com.barbud.barbudbackend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Ingredients {
    public int id;
    public String name;
    public String category;
}
