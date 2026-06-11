package com.barbud.barbudbackend.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.barbud.barbudbackend.responses.BarIngredientsResponse;
import java.util.List;

@Getter
@AllArgsConstructor
public class BarDetailsResponse {
    private String message;
    private Long id;
    private String name;
    private List<BarIngredientsResponse> ingredients;
}
