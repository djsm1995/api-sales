package com.trade.sales.features.catalog.application;

import com.trade.sales.features.catalog.domain.ports.output.CategoryRepositoryPort;
import com.trade.sales.features.catalog.infrastructure.controller.dto.CategoryRequest;
import com.trade.sales.features.catalog.infrastructure.controller.dto.CategoryResponse;
import com.trade.sales.features.catalog.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;

    /**
     * Obtiene todas las categorías con estado activo (is_active = true)
     */
    public Flux<CategoryResponse> findAllActive() {
        return categoryRepositoryPort.findByIsActiveTrue()
                .map(categoryMapper::toResponse);
    }

    public Mono<CategoryResponse> save(CategoryRequest request) {
        return Mono.just(request)
                .map(categoryMapper::toDomain) // Request -> Domain Model
                .flatMap(categoryRepositoryPort::save) // Persistir
                .map(categoryMapper::toResponse); // Domain -> Response DTO
    }

    public Mono<CategoryResponse> findById(Long id) {
        return categoryRepositoryPort.findById(id)
                .map(categoryMapper::toResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Categoría no encontrada con ID: " + id)));
    }

    public Mono<CategoryResponse> update(Long id, CategoryRequest request) {
        return categoryRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("No se puede actualizar: Categoría no existe")))
                .flatMap(category -> {
                    // Actualizamos los campos del modelo de dominio
                    category.setName(request.getName());
                    category.setDescription(request.getDescription());
                    // category.setIsActive(request.getIsActive()); // Si tu request lo permite
                    return categoryRepositoryPort.save(category);
                })
                .map(categoryMapper::toResponse);
    }

    public Mono<Void> delete(Long id) {
        return categoryRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("No se puede eliminar: Categoría no existe")))
                .flatMap(category -> categoryRepositoryPort.deleteById(id));
    }
}
