package com.trade.sales.features.catalog.infrastructure.persistence;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class ProductEntity {

    @Id
    private UUID id;
    private String sku; // Stock Keeping Unit: Código interno de inventario (ej: POL-OVR-001)
    private String barcode; // Código de barras comercial (EAN-13, UPC, etc.) para lectura con escáner
    private String name;
    private String description; // Detalle extendido de la prenda (composición, fit, cuidados)
    @Column("image_url")
    private String imageUrl;
    @Column("category_id")
    private Long categoryId; // FK hacia la tabla de categorías (ej: Calzado, Polos)
    @Column("unit_measure_id")
    private Long unitMeasureId; // FK hacia unidades de medida (ej: Par, Unidad, Kilo)
    @Column("unit_price")
    private BigDecimal unitPrice; // Precio de venta con precisión decimal (19,4) para evitar errores contables
    private String currency; // Código de moneda ISO (ej: PEN, USD)
    @Column("apply_igv")
    private Boolean applyIgv; // Flag para determinar si el precio ya incluye o debe calcular el 18% de impuesto
    private String status;

    // Campos de auditoría adicionales según tu tabla SQL
    @Column("created_by")
    private UUID createdBy;
    @Column("updated_by")
    private UUID updatedBy;

    @Column("created_at")
    @CreatedDate // Se llena solo al insertar
    private LocalDateTime createdAt;
    @Column("updated_at")
    @LastModifiedDate // Se llena solo al actualizar
    private LocalDateTime updatedAt;




}
