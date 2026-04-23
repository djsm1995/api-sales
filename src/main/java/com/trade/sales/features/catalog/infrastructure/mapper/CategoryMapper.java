package com.trade.sales.features.catalog.infrastructure.mapper;

import com.trade.sales.features.catalog.domain.models.CategoryDto;
import com.trade.sales.features.catalog.infrastructure.controller.dto.CategoryRequest;
import com.trade.sales.features.catalog.infrastructure.controller.dto.CategoryResponse;
import com.trade.sales.features.catalog.infrastructure.persistence.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    // --- De Entrada a Dominio ---
    @Mapping(target = "id", ignore = true) // El ID lo genera la DB (Auto-increment)
    CategoryDto toDomain(CategoryRequest request);

    // --- De Dominio a Entidad (R2DBC) ---
    CategoryEntity toEntity(CategoryDto domain);

    // --- De Entidad a Dominio ---
    CategoryDto toDomain(CategoryEntity entity);

    // --- De Dominio a Respuesta (Salida) ---
    CategoryResponse toResponse(CategoryDto domain);
}
