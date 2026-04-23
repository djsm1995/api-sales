package com.trade.sales.features.sales.application;

import com.trade.sales.features.sales.domain.models.Sale;
import com.trade.sales.features.sales.domain.ports.output.SaleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepositoryPort saleRepositoryPort;

    public Flux<Sale> findAllSales() {
        return saleRepositoryPort.findAll();
    }

    public Flux<Sale> findByUserId(UUID userId) {
        return saleRepositoryPort.findByUserId(userId);
    }
}
