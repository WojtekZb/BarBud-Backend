package com.barbud.barbudbackend.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BarDetailsRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long barId;
}
