package com.trade.sales.features.catalog.infrastructure.controller;

import com.trade.sales.features.catalog.application.CreateProductUseCase;
import com.trade.sales.features.catalog.application.ProductService;
import com.trade.sales.features.catalog.domain.models.Product;
import com.trade.sales.features.catalog.infrastructure.dto.ProductFullResponse;
import com.trade.sales.features.catalog.infrastructure.dto.ProductRequest;
import com.trade.sales.features.catalog.infrastructure.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Representa el artículo individual con sus propiedades técnicas y de inventario")
public class ProductController {

    private final ProductService productService;
    private final CreateProductUseCase createProductUseCase;
    private final ProductMapper mapper;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los productos", description = "Retorna un flujo reactivo de todos los productos en el catálogo")
    public Flux<ProductFullResponse> findAll() {
        return productService.getAllProducts()
                .map(mapper::toFullResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no existe")
    public Mono<ProductFullResponse> findById(@PathVariable UUID id) {
        return productService.getProductById(id)
                .map(mapper::toFullResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo producto", description = "Registra un producto genérico. El sistema valida si el SKU ya existe.")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Error en los datos o SKU duplicado")
    public Mono<ProductFullResponse> create(@RequestBody ProductRequest request, @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        Product productDomain = mapper.requestToDomain(request);
        return createProductUseCase.execute(productDomain, userId)
                .map(mapper::toFullResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente")
    public Mono<ProductFullResponse> update(@PathVariable UUID id,
                                @RequestBody ProductRequest productRequest,
                                @RequestHeader("X-User-Id") UUID userId // <-- Dinámico desde el Header
                                             ) {
        Product productDomain = mapper.requestToDomain(productRequest);
        return productService.updateProduct(id, productDomain, userId)
                .map(mapper::toFullResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un producto del catálogo")
    @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
    public Mono<Void> delete(@PathVariable UUID id) {
        return productService.deleteProduct(id);
    }
}
