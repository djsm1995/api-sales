package com.trade.sales.features.users.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String token = authToken.replace("Bearer ", "").trim();
        try {
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                return Mono.just(
                        new UsernamePasswordAuthenticationToken(
                                username, null,
                                new ArrayList<>())//TODO: para mapear los roles
                );
            }
        }catch (Exception e){
            return Mono.empty();
        }
        return Mono.empty();
    }
}
