package com.trade.sales.features.sales.domain.models;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    private UUID id;
    private UUID userId;      // Quién vendió
    private Long customerId;  // A quién
    private BigDecimal subtotal;
    private BigDecimal igvTotal;
    private BigDecimal totalAmount;
    private String paymentMethod; // EFECTIVO, YAPE, etc.
    private String status;        // Estado de la transacción (ej: COMPLETED, CANCELLED, REFUNDED)
    private String series;        // Serie del comprobante de pago (ej: B001 para boletas, F001 para facturas)
    private Integer number;       // Número correlativo del comprobante (se incrementa por cada serie)    private LocalDateTime createdAt;
    private BigDecimal igvPercentage;
    private String saleType;
    private LocalDateTime createdAt;
    private List<SaleItem> items; // El detalle

}