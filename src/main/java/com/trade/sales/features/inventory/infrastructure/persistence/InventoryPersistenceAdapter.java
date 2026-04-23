package com.trade.sales.features.inventory.infrastructure.persistence;

import com.trade.sales.features.inventory.domain.models.Stock;
import com.trade.sales.features.inventory.domain.ports.output.InventoryRepositoryPort;
import com.trade.sales.features.inventory.infrastructure.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryPersistenceAdapter implements InventoryRepositoryPort {

    private final InventoryRepository repository;
    private final InventoryMapper mapper;


    @Override
    public Mono<Stock> findByProductId(UUID productId) {
        return repository.findByProductIdAndIsAvailableTrue(uuidToBytes(productId))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Stock> save(Stock stock) {
        return repository.save(mapper.toEntity(stock))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> updateStock(UUID productId, BigDecimal quantity) {
        return repository.findByProductId(uuidToBytes(productId))
                .flatMap(entity -> {
                    entity.setCurrentStock(quantity);
                    return repository.save(entity);
                }).then();
    }

    @Override
    public Flux<Stock> findAll() {
        return repository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByProductId(UUID productId) {
        return repository.findByProductId(uuidToBytes(productId))
                .flatMap(repository::delete);
    }

    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

}
