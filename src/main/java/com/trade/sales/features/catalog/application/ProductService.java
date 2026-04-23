package com.trade.sales.features.catalog.application;

import com.trade.sales.common.exception.BusinessException;
import com.trade.sales.features.catalog.domain.models.Product;
import com.trade.sales.features.catalog.domain.ports.output.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    // Puerto de salida (Siguiendo Hexagonal, no inyectamos el R2DBC directo)
    private final ProductRepositoryPort productRepository;

    @Transactional(readOnly = true)
    public Flux<Product> getAllProducts() {
        log.info("Consultando todos los productos del catálogo");
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mono<Product> getProductById(UUID id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Producto no encontrado con ID: " + id)));
    }

//    @Transactional
//    public Mono<Product> saveProduct(Product product) {
//        // Lógica de negocio: Generar UUID si es nuevo
//        if (product.getId() == null) {
//            product.setId(UUID.randomUUID());
//        }
//
//        log.info("Registrando nuevo producto: {} con SKU: {}", product.getName(), product.getSku());
//
//        return productRepository.existsBySku(product.getSku())
//                .flatMap(exists -> {
//                    if (exists) {
//                        return Mono.error(new BusinessException("El SKU " + product.getSku() + " ya existe."));
//                    }
//                    return productRepository.save(product);
//                });
//    }

    @Transactional
    public Mono<Product> updateProduct(UUID id, Product product, UUID authenticatedUserId) {
        product.setId(id);
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    product.setId(id); // Aseguramos que mantenga el ID original
                    product.setUpdatedBy(authenticatedUserId); // <-- Seteamos el auditor
                    return productRepository.save(product);
                })
                .switchIfEmpty(Mono.error(new BusinessException("No se puede actualizar: Producto no existe")));
    }

    @Transactional
    public Mono<Void> deleteProduct(UUID id) {
        return productRepository.findById(id)
                .flatMap(product -> productRepository.deleteById(id))
                .switchIfEmpty(Mono.error(new BusinessException("No se encontró el producto para eliminar")));
    }
}
