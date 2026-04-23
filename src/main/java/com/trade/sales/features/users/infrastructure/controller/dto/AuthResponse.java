package com.trade.sales.features.users.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default  // <--- Agrega esto si tienes una inicialización
    private String type = "Bearer";
    private String username;
}
