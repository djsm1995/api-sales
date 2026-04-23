package com.trade.sales.features.catalog.infrastructure.controller;

import com.trade.sales.features.catalog.application.CategoryService;
import com.trade.sales.features.catalog.infrastructure.controller.dto.CategoryRequest;
import com.trade.sales.features.catalog.infrastructure.controller.dto.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Gestión que organiza los productos para ser consumidos por el cliente")
public class CategoryController {
    private final CategoryService categoryService; // En Application layer

    @GetMapping("/all")
    @Operation(summary = "Obtener categorias")
    @ApiResponse(responseCode = "200", description = "Categoria encontrado")
    @ApiResponse(responseCode = "404", description = "Categoria no existe")
    public Flux<CategoryResponse> getAllActive() {
        return categoryService.findAllActive();
    }

    @PostMapping("/register")
    @Operation(summary = "Guardar una nueva categoria")
    @ApiResponse(responseCode = "200", description = "Categoria encontrado")
    @ApiResponse(responseCode = "404", description = "Categoria no existe")
    public Mono<CategoryResponse> create(@RequestBody CategoryRequest request) {
        return categoryService.save(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoría por ID")
    @ApiResponse(responseCode = "200", description = "Categoría encontrada")
    @ApiResponse(responseCode = "404", description = "Categoría no existe")
    public Mono<CategoryResponse> getById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una categoría existente")
    @ApiResponse(responseCode = "200", description = "Categoría actualizada")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    public Mono<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una categoría")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito")
    public Mono<Void> delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }
}
