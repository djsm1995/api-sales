package com.trade.sales.features.inventory.domain.ports.output;

import com.trade.sales.features.inventory.domain.models.Stock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface InventoryRepositoryPort {
    Mono<Stock> findByProductId(UUID productId);
    Mono<Stock> save(Stock stock);
    Mono<Void> updateStock(UUID productId, BigDecimal quantity);
    // --- NUEVOS MÉTODOS ---
    Flux<Stock> findAll();
    Mono<Void> deleteByProductId(UUID productId);
}
