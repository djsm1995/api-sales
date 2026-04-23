package com.trade.sales.features.catalog.infrastructure.persistence;

import com.trade.sales.features.catalog.domain.models.Product;
import com.trade.sales.features.catalog.domain.ports.output.ProductRepositoryPort;
import com.trade.sales.features.catalog.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductR2dbcRepository repository;
    private final ProductMapper mapper;

    @Override
    public Mono<Product> save(Product product) {
        // 1. Asegurar ID en el dominio (Screaming Architecture)
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        // 2. Convertimos Dominio -> Entidad para que R2DBC lo entienda
        ProductEntity entityToSave = mapper.toEntity(product);
        // 3. Guardamos y convertimos de vuelta a Dominio
        return repository.save(entityToSave)
                .map(mapper::entityToDomain);
    }

    @Override
    public Mono<Boolean> existsBySku(String sku) {
        return repository.existsBySku(sku);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<Product> findAll() {
        return repository.findAll()
                .map(mapper::entityToDomain);
    }

    @Override
    public Mono<Product> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::entityToDomain);
    }
}
