package com.trade.sales.features.inventory.infrastructure.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface InventoryRepository extends ReactiveCrudRepository<InventoryEntity, Long> {

    Mono<InventoryEntity> findByProductIdAndIsAvailableTrue(byte[] productId);
    Mono<InventoryEntity> findByProductId(byte[] productId);

    @Query("UPDATE inventory SET current_stock = :quantity, updated_at = CURRENT_TIMESTAMP WHERE product_id = :productId")
    Mono<Void> updateStockQuantity(UUID productId, BigDecimal quantity);
}
