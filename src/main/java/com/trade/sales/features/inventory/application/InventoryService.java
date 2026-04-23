package com.trade.sales.features.inventory.application;

import com.trade.sales.common.exception.BusinessException;
import com.trade.sales.common.security.SecurityUtils;
import com.trade.sales.features.inventory.domain.models.Stock;
import com.trade.sales.features.inventory.domain.ports.output.InventoryRepositoryPort;
import com.trade.sales.features.sales.infrastructure.dto.SaleItemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepositoryPort inventoryRepository;
    private final SecurityUtils securityUtils;
    /**
     * MÉTODO PARA CAMEL (PUENTE - CamelRoutes)
     * Camel no entiende de Monos por defecto, así que bloqueamos
     * o nos suscribimos para asegurar la ejecución asíncrona.
     */
    public void initializeInventoryFromCamel(UUID productId) {
        log.info("Iniciando creación de stock para producto: {}", productId);
        // Usamos .subscribe() para que se ejecute en segundo plano sin bloquear el hilo de Camel
        this.increaseStock(productId, BigDecimal.ZERO)
                .doOnSuccess(s -> log.info("Stock inicial creado para: {}", productId))
                .doOnError(e -> log.error("Error al inicializar stock: {}", e.getMessage()))
                .subscribe();
    }

    @Transactional
    public Mono<Stock> increaseStock(UUID productId, BigDecimal amount) {
        return securityUtils.getCurrentUserId() // 1. Obtenemos quién hace la carga
                .flatMap(userId ->
                        inventoryRepository.findByProductId(productId)
                                .flatMap(stock -> {
                                    // Actualizamos stock existente
                                    stock.setCurrentStock(stock.getCurrentStock().add(amount));
                                    stock.setModifiedBy(userId); // Seteamos el auditor
                                    stock.setUpdatedAt(LocalDateTime.now());
                                    return inventoryRepository.save(stock);
                                })
                                // Si no existe, creamos el inicial pasando también el userId
                                .switchIfEmpty(Mono.defer(() -> createInitialStock(productId, amount, userId)))
                );
    }

    @Transactional
    public Mono<Stock> decreaseStock(SaleItemRequest item) {
        return securityUtils.getCurrentUserId() // 1. Obtenemos quién autoriza la salida manual
                .flatMap(userId ->
                        inventoryRepository.findByProductId(item.getProductId())
                                .flatMap(inventory -> {
                                    BigDecimal newStock = inventory.getCurrentStock().subtract(item.getQuantity());

                                    // Validación básica de seguridad de negocio
                                    if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                                        return Mono.error(new RuntimeException("Stock insuficiente para realizar la operación"));
                                    }

                                    inventory.setCurrentStock(newStock);
                                    inventory.setModifiedBy(userId); // Auditoría
                                    inventory.setUpdatedAt(LocalDateTime.now());
                                    return inventoryRepository.save(inventory);
                                })
                );
    }

    // --- NUEVO: BUSCAR POR ID DE PRODUCTO ---
    public Mono<Stock> findByProductId(UUID productId) {
        return inventoryRepository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new BusinessException(
                        "No se encontró inventario para el producto: " + productId,
                        HttpStatus.NOT_FOUND)));
    }

    // --- NUEVO: LISTAR TODO EL INVENTARIO ---
    public Flux<Stock> findAll() {
        return inventoryRepository.findAll()
                .doOnTerminate(() -> log.info("Consulta de inventario completo finalizada"));
    }
    @Transactional
    public Mono<Void> deleteInventory(UUID productId) {
        log.info("Eliminando registro de inventario para el producto: {}", productId);
        return inventoryRepository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new BusinessException("El inventario no existe", HttpStatus.NOT_FOUND)))
                .flatMap(stock -> {
                    // Opcional: Impedir borrado si hay stock positivo
                    if (stock.getCurrentStock().compareTo(BigDecimal.ZERO) > 0) {
                        return Mono.error(new BusinessException("No se puede eliminar inventario con stock actual mayor a 0"));
                    }
                    return inventoryRepository.deleteByProductId(productId);
                });
    }

    private Mono<Stock> createInitialStock(UUID productId, BigDecimal amount, UUID userId) {
        Stock newStock = Stock.builder()
//                .id(UUID.randomUUID())
                .productId(productId)
                .currentStock(amount)
                .minStock(new BigDecimal("5.00")) // Valor por defecto del negocio
                .location("ALMACEN_CENTRAL")     // Ubicación inicial por defecto
                .isAvailable(true)
                .createdBy(userId)               // Quién lo registra inicialmente
                .modifiedBy(null)                // Quién creó el registro
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return inventoryRepository.save(newStock);
    }
}
