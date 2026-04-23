package com.trade.sales.features.catalog.domain.ports.output;

import com.trade.sales.features.catalog.domain.models.CategoryDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CategoryRepositoryPort {

    Flux<CategoryDto> findByIsActiveTrue();
    Mono<CategoryDto> save(CategoryDto category);
    Mono<CategoryDto> findById(Long id);
    Mono<Void> deleteById(Long id);

}
