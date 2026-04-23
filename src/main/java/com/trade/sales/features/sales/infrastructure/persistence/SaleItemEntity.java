package com.trade.sales.features.sales.infrastructure.persistence;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sale_items")
public class SaleItemEntity {

    @Id
    private UUID id;

    @Column("sale_id") // FK hacia la tabla sales
    private UUID saleId;

    @Column("product_id") // FK hacia la tabla products
    private UUID productId;

    private BigDecimal quantity;

    @Column("unit_price") // Precio histórico capturado
    private BigDecimal unitPrice;

    @Column("discount_amount")
    private BigDecimal discountAmount;

    @Column("total_item")
    private BigDecimal totalItem;
}
