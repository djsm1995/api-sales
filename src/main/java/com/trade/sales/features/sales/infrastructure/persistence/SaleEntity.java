package com.trade.sales.features.sales.infrastructure.persistence;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
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
@Table("sales")
public class SaleEntity {
    @Id
    private UUID id;

    @Column("user_id") // Coincide con BINARY(16) user_id
    private UUID userId;

    @Column("customer_id") // Coincide con BIGINT customer_id
    private Long customerId;

    private BigDecimal subtotal;

    @Column("igv_total")
    private BigDecimal igvTotal;

    @Column("total_amount")
    private BigDecimal totalAmount;

    @Column("payment_method")
    private String paymentMethod; // Método de pago utilizado (ej: EFECTIVO, TARJETA, YAPE, PLIN)
    private String status;        // Estado de la transacción (ej: COMPLETED, CANCELLED, REFUNDED)
    private String series;        // Serie del comprobante de pago (ej: B001 para boletas, F001 para facturas)
    private Integer number;       // Número correlativo del comprobante (se incrementa por cada serie)
    @Column("igv_percentage")
    private BigDecimal igvPercentage;
    @Column("sale_type")
    private String saleType;

    @Column("created_at")
    @CreatedDate // Se llena solo al insertar
    private LocalDateTime createdAt;
}