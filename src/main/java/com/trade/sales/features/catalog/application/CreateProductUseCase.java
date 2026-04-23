package com.trade.sales.features.catalog.application;

import com.trade.sales.features.catalog.domain.models.Product;
import com.trade.sales.features.catalog.domain.ports.output.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateProductUseCase {

    private final ProductRepositoryPort repository;
    private final ProducerTemplate producerTemplate; // El "teléfono" de Camel

    public Mono<Product> execute(Product product, UUID creatorId) {
        return repository.existsBySku(product.getSku())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("El SKU ya existe: " + product.getSku()));
                    }
                    product.setCreatedBy(creatorId);
                    product.setStatus("ACTIVO");
                    return repository.save(product);
                })
                .doOnSuccess(savedProduct -> {
                    log.info("Producto creado con ID: {}. Notificando a Inventario...", savedProduct.getId());
                    // Enviamos el objeto al canal 'direct:newProduct' definido en CamelRoutes
                    producerTemplate.sendBody("direct:newProduct", savedProduct);
                })
                .doOnError(e -> log.error("Error al crear producto: {}", e.getMessage()));
    }
}
