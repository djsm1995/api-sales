package com.trade.sales.features.users.infrastructure.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {

    // Para el Login: Buscamos por nombre de usuario
    Mono<UserEntity> findByUsername(String username);

    // Para validaciones: ¿Existe ya este DNI o Email?
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByDni(String dni);
}
