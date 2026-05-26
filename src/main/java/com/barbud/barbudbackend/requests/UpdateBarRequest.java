package com.barbud.barbudbackend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateBarRequest {
    private Long userId;
    private Long barId;
    private String name;
    private List<Long> ingredientIds;
}
