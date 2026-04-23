package com.trade.sales.features.users.infrastructure.controller;

import com.trade.sales.features.users.application.AuthenticateUserUseCase;
import com.trade.sales.features.users.infrastructure.controller.dto.AuthResponse;
import com.trade.sales.features.users.infrastructure.controller.dto.LoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints para login y gestión de sesiones")
public class AuthController {

    // Nota: Necesitarás crear este UseCase para manejar la lógica de BCrypt y JWT
     private final AuthenticateUserUseCase authUseCase;

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authUseCase.execute(request)
                .map(token -> AuthResponse.builder()
                        .token(token)
                        .username(request.getUsername())
                        .build());
    }
}
