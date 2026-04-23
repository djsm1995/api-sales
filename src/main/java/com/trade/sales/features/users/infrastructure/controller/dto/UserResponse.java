package com.trade.sales.features.users.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String id;        // El UUID convertido a String
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private String message;   // Un mensaje de confirmación tipo "Usuario creado"
    // Opcional: puedes incluir la fecha de creación si la necesitas en el front
    // private LocalDateTime createdAt;
}