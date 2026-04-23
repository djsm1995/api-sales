package com.trade.sales.features.users.infrastructure.controller;

import com.trade.sales.features.users.application.UserService;
import com.trade.sales.features.users.domain.User;
import com.trade.sales.features.users.infrastructure.controller.dto.UserResponse;
import com.trade.sales.features.users.infrastructure.dto.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operaciones de administración de usuarios")
public class UserController {
    private final UserService userService;

    @GetMapping("list")
    @Operation(summary = "Listar todos los usuarios registrados")
    public Flux<User> findAll() {
        return userService.listUsers();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar nuevo usuario (Cajero/Admin)")
    public Mono<UserResponse> register(@RequestBody UserRequest userRequest) {
        return userService.register(userRequest);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un usuario existente")
    public Mono<UserResponse> update(@PathVariable UUID id, @RequestBody UserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario del sistema")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }
}
