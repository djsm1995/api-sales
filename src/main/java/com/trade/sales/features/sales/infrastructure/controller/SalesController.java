package com.trade.sales.features.sales.infrastructure.controller;

import com.trade.sales.features.sales.application.ProcessSaleUseCase;
import com.trade.sales.features.sales.application.FindSaleByIdUseCase;
import com.trade.sales.features.sales.application.SaleService;
import com.trade.sales.features.sales.domain.models.Sale;
import com.trade.sales.features.sales.infrastructure.controller.dto.SaleResponse;
import com.trade.sales.features.sales.infrastructure.mapper.SaleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Operaciones de transacciones de ventas")
public class SalesController {

    private final SaleService saleService;
    private final ProcessSaleUseCase processSaleUseCase;
    private final FindSaleByIdUseCase findSaleByIdUseCase;
    private final SaleMapper saleMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar una nueva venta (Procesar transacción)")
    public Mono<Sale> create(@RequestBody Sale saleRequest) {
        // El UseCase se encarga de la lógica: validar stock, calcular totales y guardar
        return processSaleUseCase.execute(saleRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle de una venta por su ID")
    public Mono<Sale> findById(@PathVariable UUID id) {
        return findSaleByIdUseCase.execute(id);
    }

    @GetMapping("/all")
    public Flux<SaleResponse> getAllSales() {
        return saleService.findAllSales()
                .map(saleMapper::toResponse);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar ventas realizadas por un usuario específico")
    public Flux<SaleResponse> getByUserId(@PathVariable UUID userId) {
        return saleService.findByUserId(userId)
                .map(saleMapper::toResponse); // Transformación de Flux<Sale> a Flux<SaleResponse>
    }
}
