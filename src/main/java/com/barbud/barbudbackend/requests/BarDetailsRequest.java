package com.barbud.barbudbackend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BarDetailsRequest {
    private Long userId;
    private Long barId;
}
