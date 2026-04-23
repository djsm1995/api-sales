package com.trade.sales.features.users.infrastructure.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserEntity {

    @Id
    private UUID id;
    private String username;
    private String password;
    private String email;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    private String dni;
    // Guardamos el nombre del Enum como String en la base de datos
    private String role;
    @Column("is_active")
    private Boolean isActive;
    @Column("last_login")
    private LocalDateTime lastLogin; // Registro de auditoría de la última sesión exitosa del usuario

    @CreatedDate // Se llena solo al insertar
    @Column("created_at")
    private LocalDateTime createdAt; // Fecha y hora exacta de creación del perfil del usuario

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt; // Rastreo de la última actualización de credenciales o perfil

 }