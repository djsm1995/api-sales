package com.trade.sales.features.sales.infrastructure.persistence;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SaleItemR2dbcRepository extends R2dbcRepository<SaleItemEntity, Long> {
    Flux<SaleItemEntity> findAllBySaleId(UUID saleId);
}
