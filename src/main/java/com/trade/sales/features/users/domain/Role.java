package com.trade.sales.features.users.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ADMIN"),
    CASHIER("CASHIER"),
    USER("USER");

    private final String value;

    /**
     * Utilidad para convertir un String de la base de datos al Enum.
     */
    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.value.equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Rol no soportado: " + role);
    }
}