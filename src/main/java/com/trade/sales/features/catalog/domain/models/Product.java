package com.trade.sales.features.catalog.domain.models;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Product {
    private UUID id;
    private String sku; // Stock Keeping Unit: Código interno de inventario (ej: POL-OVR-001)
    private String barcode; // Código de barras comercial (EAN-13, UPC, etc.) para lectura con escáner
    private String name;
    private String description; // Detalle extendido de la prenda (composición, fit, cuidados)
    private String imageUrl;
    private Long categoryId; // FK hacia la tabla de categorías (ej: Calzado, Polos)
    private Long unitMeasureId; // FK hacia unidades de medida (ej: Par, Unidad, Kilo)
    private BigDecimal unitPrice; // Precio de venta con precisión decimal (19,4) para evitar errores contables
    private String currency; // Código de moneda ISO (ej: PEN, USD)
    private Boolean applyIgv; // Flag para determinar si el precio ya incluye o debe calcular el 18% de impuesto
    private String status;
    // Para auditoría
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
