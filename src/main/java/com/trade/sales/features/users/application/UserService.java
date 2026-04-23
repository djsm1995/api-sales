package com.trade.sales.features.users.application;

import com.trade.sales.common.exception.BusinessException;
import com.trade.sales.features.users.domain.Role;
import com.trade.sales.features.users.domain.User;
import com.trade.sales.features.users.infrastructure.controller.dto.UserResponse;
import com.trade.sales.features.users.infrastructure.dto.UserRequest;
import com.trade.sales.features.users.infrastructure.mapper.UserMapper;
import com.trade.sales.features.users.infrastructure.persistence.UserEntity;
import com.trade.sales.features.users.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final R2dbcEntityTemplate entityTemplate;
    private final PasswordEncoder passwordEncoder;

    public Flux<User> listUsers() {
        return userRepository.findAll()
                .map(userMapper::toDomain);
    }

    public Mono<UserResponse> register(UserRequest request) {
        User user = userMapper.toDomain(request);
        return userRepository.existsByUsername(user.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessException("El nombre de usuario ya existe"));
                    }

                    // Creamos la entidad
                    UserEntity userEntity = UserEntity.builder()
                            .id(UUID.randomUUID()) // Generamos el ID aquí
                            .username(user.getUsername())
                            .password(passwordEncoder.encode(user.getPassword())) // CIFRADO
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .dni(user.getDni())
                            .role(user.getRole() != null ? user.getRole().name() : Role.USER.name())
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .build();

//                    return userRepository.save(userEntity);
                    return entityTemplate.insert(userEntity);
                })
                .map(savedUser -> UserResponse.builder()
                        .id(savedUser.getId().toString())
                        .username(savedUser.getUsername())
                        .firstName(savedUser.getFirstName())
                        .message("Usuario registrado exitosamente")
                        .build());
    }

    public Mono<UserResponse> updateUser(UUID id, UserRequest request) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Usuario no encontrado con ID: " + id)))
                .flatMap(existingUser -> {
                    existingUser.setFirstName(request.getFirstName());
                    existingUser.setLastName(request.getLastName());
                    existingUser.setEmail(request.getEmail());
                    existingUser.setDni(request.getDni());
                    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                })
                .map(updatedUser -> UserResponse.builder()
                        .id(updatedUser.getId().toString())
                        .username(updatedUser.getUsername())
                        .firstName(updatedUser.getFirstName())
                        .message("Usuario actualizado exitosamente")
                        .build());
    }

    public Mono<Void> deleteUser(UUID id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("No se puede eliminar: Usuario no encontrado")))
                .flatMap(user -> userRepository.deleteById(id));
    }

}
