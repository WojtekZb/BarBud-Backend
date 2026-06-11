package com.barbud.barbudbackend.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateBarRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String name;

    @NotNull
    private List<Long> ingredientIds;
}