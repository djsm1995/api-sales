package com.trade.sales.features.sales.domain.ports.output;

import com.trade.sales.features.sales.domain.models.Sale;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface SaleRepositoryPort {
    Mono<Sale> save(Sale sale);
//    Mono<Sale> findById(UUID id);
    Flux<Sale> findAll();
    Flux<Sale> findByUserId(UUID userId);
}
