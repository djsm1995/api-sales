package com.trade.sales.features.catalog.infrastructure.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CategoryR2dbcRepository extends ReactiveCrudRepository<CategoryEntity, Long> {

    // Método para el endpoint getAllActive() del Controller
    Flux<CategoryEntity> findByIsActiveTrue();
}
