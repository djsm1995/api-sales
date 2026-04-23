package com.trade.sales.features.users.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String dni;
    private Role role; // Usamos el Enum aquí
    private Boolean isActive;
    private LocalDateTime lastLogin; // Registro de auditoría de la última sesión exitosa del usuario
    private LocalDateTime createdAt; // Fecha y hora exacta de creación del perfil del usuario
    private LocalDateTime updatedAt; // Rastreo de la última actualización de credenciales o perfil

}
