package com.trade.sales.features.sales.application;

import com.trade.sales.common.security.SecurityUtils;
import com.trade.sales.features.sales.domain.models.Sale;
import com.trade.sales.features.sales.domain.ports.output.SaleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessSaleUseCase {

    private final SaleRepositoryPort saleRepository; // Tu puerto de persistencia
    private final ProducerTemplate producerTemplate; // El "teléfono" de Camel
    private final SecurityUtils securityUtils;

    @Transactional
    public Mono<Sale> execute(Sale sale) {
        return securityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    if (sale.getId() == null) sale.setId(UUID.randomUUID());
                    sale.setUserId(userId);
                    sale.setCreatedAt(LocalDateTime.now());
                    sale.setStatus("COMPLETED");
                    BigDecimal igvRate = new BigDecimal("0.18");
                    BigDecimal igvTotal = sale.getSubtotal().multiply(igvRate);

                    sale.setIgvPercentage(new BigDecimal("18.00"));
                    sale.setIgvTotal(igvTotal);
                    sale.setTotalAmount(sale.getSubtotal().add(igvTotal));
                    log.info("Procesando venta: {} por el vendedor: {} por un total de {}", sale.getId(), userId, sale.getTotalAmount());
                    return saleRepository.save(sale);
                })
                .doOnSuccess(savedSale -> {
                    // 4. Notificar a Inventario vía Apache Camel
                    // Aquí podrías enviar el savedSale completo si Camel necesita el sellerId
                    producerTemplate.sendBody("direct:decreaseStock", savedSale.getItems());
                    log.info("Venta guardada. Notificación de stock enviada a Camel.");
                })
                .doOnError(e -> log.error("Error al procesar venta: {}", e.getMessage()));
    }
}
