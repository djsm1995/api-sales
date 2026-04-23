package com.trade.sales.features.sales.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {
    private String id;
    private String userId;
    private Long customerId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private String createdAt; // Recuerda mapearlo como String en SaleMapper para el formato de Perú
}

