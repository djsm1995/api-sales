package com.trade.sales.common.security;

import com.trade.sales.features.users.infrastructure.security.UserPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class SecurityUtils {
    public Mono<UUID> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .cast(UserPrincipal.class) // Cast a TU clase personalizada
                .map(UserPrincipal::getId)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no autenticado")));
    }
}