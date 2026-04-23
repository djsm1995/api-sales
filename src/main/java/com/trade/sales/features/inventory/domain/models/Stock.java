package com.trade.sales.features.inventory.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stock {
    private Long id;
    private UUID productId;
    private BigDecimal currentStock;// Cantidad física actual en almacén (soporta decimales para peso/fracción)
    private BigDecimal minStock;// Nivel crítico para alertas de reposición (Stock mínimo de seguridad)
    private String location; // Ubicación física detallada (ej: Estante A-1, Almacén Central, Vitrina)
    private String batchNumber;// Número de lote o temporada (ej: LOTE-2026-001 o VERANO-26)
    private LocalDateTime expirationDate; // Fecha de vencimiento (opcional para productos perecederos o promociones)
    private Boolean isAvailable; // Flag lógico para bloquear stock (ej: mercadería dañada o reservada)
    /*campos auditoria*/
    private LocalDateTime lastEntryDate; // Registro de la última vez que se aumentó stock (abastecimiento)
    private UUID modifiedBy; // ID del último usuario que modificó el stock (ej: por venta o ajuste)
    private UUID createdBy; // Usuario que ingreso el producto al inventario
    private LocalDateTime createdAt;// Fecha de creación del registro de stock en el sistema
    private LocalDateTime updatedAt;// Fecha de cualquier cambio en el stock, ubicación o disponibilidad
}
