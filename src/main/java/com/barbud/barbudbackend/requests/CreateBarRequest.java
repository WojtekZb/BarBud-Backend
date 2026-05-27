package com.barbud.barbudbackend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateBarRequest {
    private Long userId;
    private String name;
    private List<Long> ingredientIds;
}