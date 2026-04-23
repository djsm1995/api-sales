package com.trade.sales.config;

import com.trade.sales.features.users.application.JwtAuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

private final JwtAuthenticationManager jwtAuthenticationManager;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // 1. Definimos el filtro
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(jwtAuthenticationManager);

        jwtFilter.setRequiresAuthenticationMatcher(exchange ->
                ServerWebExchangeMatchers.pathMatchers("/api/v1/**").matches(exchange)
        );
        // 2. CONVERTIDOR: Extrae el token
        jwtFilter.setServerAuthenticationConverter(exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(org.springframework.http.HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // IMPORTANTE: Pasamos el token como credencial
                return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
            }
            return Mono.empty();
        });
        // ÉXITO Y FALLO: Esto evita el error 500 si el token no es correcto
        jwtFilter.setAuthenticationFailureHandler((webFilterExchange, exception) ->
                Mono.fromRunnable(() -> webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
        );
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                // Esto elimina el popup de "Iniciar sesión" del navegador
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                // ESTO MATA EL POPUP: Le dice a Spring: "Si no están autorizados, no lances el diálogo de login"
//                .exceptionHandling(exceptionHandling -> exceptionHandling
//                        .authenticationEntryPoint((exchange, e) -> {
//                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                            return Mono.empty();
//                        })
//                )
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/api/v1/auth/**",
//                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**",// 2. Permitir Swagger UI y OpenAPI Docs (Vital para el desarrollo)
//                                "/actuator/**").permitAll()
//                        .anyExchange().authenticated()// 4. El resto requiere autenticación
//                )
//                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Stateless
//                .build();
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Desactivamos CSRF (necesario para POST/PUT en APIs)
                .authorizeExchange(exchanges -> exchanges
                        // Permitimos TODO por ahora para facilitar las pruebas de los Slices
                        .anyExchange().permitAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }
}