package com.trade.sales.features.catalog.infrastructure.persistence;

import com.trade.sales.features.catalog.domain.models.CategoryDto;
import com.trade.sales.features.catalog.domain.ports.output.CategoryRepositoryPort;
import com.trade.sales.features.catalog.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component // Esta anotación es la que resuelve el error de "Bean not found"
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryR2dbcRepository repository;
    private final CategoryMapper mapper;

    @Override
    public Flux<CategoryDto> findByIsActiveTrue() {
        return repository.findByIsActiveTrue()
                .map(mapper::toDomain)
                .doOnError(e -> log.error("Error al listar categorías activas: {}", e.getMessage()));
    }

    @Override
    public Mono<CategoryDto> save(CategoryDto category) {
        return Mono.just(category)
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDomain)
                .doOnSuccess(c -> log.info("Categoría guardada con éxito: {}", c.getName()));
    }

    @Override
    public Mono<CategoryDto> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .switchIfEmpty(Mono.empty())
                .doOnError(e -> log.error("Error al buscar categoría por Long ID {}: {}", id, e.getMessage()));
    }

//    @Override
//    public Mono<CategoryDto> findById(UUID id) {
//        // Si tu tabla usa UUID, R2DBC lo maneja directamente.
//        // Si no lo usas, puedes dejarlo que lance una excepción o devolver Mono.empty()
//        return repository.findById(id)
//                .map(mapper::toDomain)
//                .doOnError(e -> log.error("Error al buscar categoría por UUID {}: {}", id, e.getMessage()));
//    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id)
                .doOnSuccess(v -> log.info("Registro de categoría con ID {} eliminado", id))
                .doOnError(e -> log.error("Error al eliminar categoría {}: {}", id, e.getMessage()));
    }
}
