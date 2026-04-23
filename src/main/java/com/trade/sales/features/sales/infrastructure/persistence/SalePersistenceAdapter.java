package com.trade.sales.features.sales.infrastructure.persistence;

import com.trade.sales.features.sales.domain.models.Sale;
import com.trade.sales.features.sales.domain.ports.output.SaleRepositoryPort;
import com.trade.sales.features.sales.infrastructure.mapper.SaleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SalePersistenceAdapter implements SaleRepositoryPort {

    private final SaleR2dbcRepository saleRepository;
    private final SaleItemR2dbcRepository itemRepository;
    private final SaleMapper mapper;

    @Override
    @Transactional // Importante: Asegura que si falla un item, no se cree la cabecera
    public Mono<Sale> save(Sale sale) {
        // 1. Convertir dominio a entidad (Cabecera)
        SaleEntity saleEntity = mapper.toEntity(sale);

        // 2. Guardar la cabecera
        return saleRepository.save(saleEntity)
                .flatMap(savedHeader -> {
                    // 3. Preparar los items con el ID de la venta generada
                    List<SaleItemEntity> itemEntities = sale.getItems().stream()
                            .map(item -> {
                                SaleItemEntity itemEntity = mapper.toItemEntity(item);// UUID para cada línea de detall
                                itemEntity.setId(UUID.randomUUID());
                                itemEntity.setSaleId(savedHeader.getId());// FK vinculada
                                return itemEntity;
                            })
                            .toList();

                    // 4. Guardar todos los items y retornar el objeto de dominio completo
                    return itemRepository.saveAll(itemEntities)
                            .collectList()
                            .map(savedItems -> {
                                Sale result = mapper.toDomain(savedHeader);
                                result.setItems(mapper.toItemDomainList(savedItems));
                                return result;
                            });
                });
    }

    @Override
    public Flux<Sale> findAll() {
        return saleRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Sale> findByUserId(UUID userId) {
        return saleRepository.findByUserId(userId)
                .map(mapper::toDomain);
                //.doOnComplete(() -> log.info("Búsqueda de ventas para el usuario {} completada", userId));
    }
}
