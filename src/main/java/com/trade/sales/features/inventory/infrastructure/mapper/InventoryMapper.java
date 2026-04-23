package com.trade.sales.features.inventory.infrastructure.mapper;

import com.trade.sales.features.inventory.domain.models.Stock;
import com.trade.sales.features.inventory.infrastructure.persistence.InventoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.nio.ByteBuffer;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {

//    @Mapping(source = "currentStock", target = "quantity")
    Stock toDomain(InventoryEntity entity);

//    @Mapping(source = "quantity", target = "currentStock")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastEntryDate", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
//    @Mapping(target = "productId", expression = "java(uuidToBytes(domain.getProductId()))")
//    @Mapping(target = "expirationDate", ignore = true)
//    @Mapping(target = "minStock", constant = "5") // Valor por defecto del SQL
    InventoryEntity toEntity(Stock domain);

    default UUID map(byte[] value) {
        if (value == null) return null;
        ByteBuffer bb = ByteBuffer.wrap(value);
        return new UUID(bb.getLong(), bb.getLong());
    }

    default byte[] map(UUID value) {
        if (value == null) return null;
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(value.getMostSignificantBits());
        bb.putLong(value.getLeastSignificantBits());
        return bb.array();
    }
}
