package com.trade.sales.features.users.infrastructure.mapper;

import com.trade.sales.features.users.domain.User;
import com.trade.sales.features.users.infrastructure.persistence.UserEntity;
import com.trade.sales.features.users.infrastructure.dto.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", expression = "java(com.trade.sales.features.users.domain.Role.fromString(request.getRole().getValue()))")
    User toDomain(UserRequest request);
}
