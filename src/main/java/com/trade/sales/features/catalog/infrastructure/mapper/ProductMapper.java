package com.trade.sales.features.catalog.infrastructure.mapper;

import com.trade.sales.features.catalog.domain.models.Product;
import com.trade.sales.features.catalog.infrastructure.dto.ProductFullResponse;
import com.trade.sales.features.catalog.infrastructure.dto.ProductRequest;
import com.trade.sales.features.catalog.infrastructure.persistence.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    Product requestToDomain(ProductRequest request);
    Product entityToDomain(ProductEntity entity);
    ProductFullResponse toFullResponse(Product domain);
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(Product domain);

    // Añade esto para que MapStruct sepa qué hacer con la URI
    default String mapUriToString(URI value) {
        return value != null ? value.toString() : null;
    }

    // Si la conversión es de String a URI (en el otro sentido)
    default URI mapStringToUri(String value) {
        return value != null ? URI.create(value) : null;
    }
    /**
     * Este método resuelve el ERROR de compilación:
     * "Can't map property LocalDateTime to OffsetDateTime"
     */
    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        // Convierte LocalDateTime a OffsetDateTime usando UTC (o el offset que prefieras)
        return value.atOffset(ZoneOffset.UTC);
    }
}