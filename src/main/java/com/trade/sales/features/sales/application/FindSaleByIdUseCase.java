package com.trade.sales.features.sales.application;

import com.trade.sales.common.exception.BusinessException;
import com.trade.sales.features.sales.domain.models.Sale;
import com.trade.sales.features.sales.infrastructure.mapper.SaleMapper;
import com.trade.sales.features.sales.infrastructure.persistence.SaleR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindSaleByIdUseCase {

    private final SaleR2dbcRepository saleRepository;
    private final SaleMapper saleMapper;

    public Mono<Sale> execute(UUID id) {
        return saleRepository.findById(id)
                .map(saleMapper::toDomain)
                .switchIfEmpty(Mono.error(new BusinessException("La venta con ID " + id + " no existe")));
    }
}