package com.trade.sales.features.inventory.infrastructure.persistence;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inventory")
public class InventoryEntity {
    @Id
    private Long id;

    @Column("product_id")
    private UUID productId;

    @Column("current_stock")
    private BigDecimal currentStock; // Cantidad física actual en almacén (soporta decimales para peso/fracción)

    @Column("min_stock")
    private BigDecimal minStock; // Nivel crítico para alertas de reposición (Stock mínimo de seguridad)
    private String location;// Ubicación física detallada (ej: Estante A-1, Almacén Central, Vitrina)

    @Column("batch_number")
    private String batchNumber;// Número de lote o temporada (ej: LOTE-2026-001 o VERANO-26)
    @Column("expiration_date")
    private LocalDate expirationDate;// Fecha de vencimiento (opcional para productos perecederos o promociones)
    @Column("is_available")
    private Boolean isAvailable; // Flag lógico para bloquear stock (ej: mercadería dañada o reservada)

    @Column("last_entry_date")
    private LocalDateTime lastEntryDate; // Registro de la última vez que se aumentó stock (abastecimiento)

    @CreatedBy
    @Column("created_by")
    private UUID createdBy; // Usuario que ingreso el producto al inventario

    @LastModifiedBy
    @Column("modified_by")
    private UUID modifiedBy; // ID del último usuario que modificó el stock (ej: por venta o ajuste)

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;// Fecha de creación del registro de stock en el sistema

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;// Fecha de cualquier cambio en el stock, ubicación o disponibilidad
}