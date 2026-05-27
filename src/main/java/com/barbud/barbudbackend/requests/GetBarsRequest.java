package com.barbud.barbudbackend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetBarsRequest {
    private Long userId;
}
