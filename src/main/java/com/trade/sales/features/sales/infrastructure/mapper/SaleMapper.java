package com.trade.sales.features.sales.infrastructure.mapper;

import com.trade.sales.features.sales.domain.models.Sale;
import com.trade.sales.features.sales.domain.models.SaleItem;
import com.trade.sales.features.sales.infrastructure.controller.dto.SaleResponse;
import com.trade.sales.features.sales.infrastructure.persistence.SaleEntity;
import com.trade.sales.features.sales.infrastructure.persistence.SaleItemEntity;
import com.trade.sales.features.sales.infrastructure.dto.SaleItemRequest; // El generado por OpenAPI
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        builder = @Builder(disableBuilder = true))
public interface SaleMapper {


    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "dd-MM-yyyy HH:mm:ss")
        // MapStruct convierte automáticamente UUID a String si los nombres de campos coinciden
    SaleResponse toResponse(Sale domain);

    // Si tu servicio devuelve la Entidad directamente:
    SaleResponse entityToResponse(SaleEntity entity);

    // --- DOMAIN <-> ENTITY (Persistence) ---
    @Mapping(target = "items", ignore = true) // Los items se cargan por separado en el Adapter
    Sale toDomain(SaleEntity entity);
    SaleEntity toEntity(Sale domain);

    @Mapping(target = "id", ignore = true) // Se asigna manualmente en el PersistenceAdapter
    SaleItemEntity toItemEntity(SaleItem domain);

    @Mapping(target = "id", source = "id")
    SaleItem toItemDomain(SaleItemEntity entity);

    List<SaleItem> toItemDomainList(List<SaleItemEntity> entities);

    // --- DTO -> DOMAIN (Entry Point) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unitPrice", source = "unitPrice") // Asegúrate que tu SaleItemRequest ya tenga unitPrice
    SaleItem toItemDomainFromRequest(SaleItemRequest request);

    List<SaleItem> toItemDomainListFromRequests(List<SaleItemRequest> requests);
}
