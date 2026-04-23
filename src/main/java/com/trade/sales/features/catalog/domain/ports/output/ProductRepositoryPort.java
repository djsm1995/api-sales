package com.trade.sales.features.catalog.domain.ports.output;

import com.trade.sales.features.catalog.domain.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepositoryPort {
    Flux<Product> findAll();
    Mono<Product> findById(UUID id);
    Mono<Product> save(Product product);
    Mono<Boolean> existsBySku(String sku);
    Mono<Void> deleteById(UUID id);
}
