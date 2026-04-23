package com.trade.sales.features.inventory.infrastructure.controller;

import com.trade.sales.features.inventory.application.InventoryService;
import com.trade.sales.features.inventory.domain.models.Stock;
import com.trade.sales.features.inventory.infrastructure.mapper.InventoryMapper;
import com.trade.sales.features.sales.infrastructure.dto.SaleItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Control de stock y almacén")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @PatchMapping("/increase/{productId}")
    @Operation(summary = "Cargar stock (Entrada de mercadería)")
    public Mono<Stock> increase(@PathVariable UUID productId, @RequestParam BigDecimal amount) {
        return inventoryService.increaseStock(productId, amount);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Ver stock actual de un producto especifico")
    public Mono<Stock> getStockForId(@PathVariable UUID productId) {
        return inventoryService.findByProductId(productId);
    }

    @GetMapping("/all")
    @Operation(summary = "Ver stock actual de un producto especifico")
    public Flux<Stock> getAllStock() {
        return inventoryService.findAll();
    }

    @PatchMapping("/decrease/{productId}")
    @Operation(summary = "Salida por Venta (No puedes vender lo que no tienes) - Salida de stock manual")
    public Mono<Stock> decrease(@RequestBody SaleItemRequest item){
        return inventoryService.decreaseStock(item);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Eliminar físicamente el registro de inventario")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID productId) {
        return inventoryService.deleteInventory(productId);
    }


}
