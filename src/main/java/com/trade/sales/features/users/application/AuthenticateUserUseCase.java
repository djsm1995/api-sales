package com.trade.sales.features.users.application;

import com.trade.sales.common.exception.BusinessException;
import com.trade.sales.features.users.infrastructure.controller.dto.LoginRequest;
import com.trade.sales.features.users.infrastructure.mapper.UserMapper;
import com.trade.sales.features.users.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Mono<String> execute(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .flatMap(userEntity -> {
                    // Validar contraseña
                    if (passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
                        return Mono.just(jwtService.generateToken(userEntity.getUsername()));
                    } else {
                        return Mono.error(new BusinessException("Credenciales inválidas"));
                    }
                })
                .switchIfEmpty(Mono.error(new BusinessException("El usuario no existe")));
    }
}
