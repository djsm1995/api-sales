package com.trade.sales.features.users.infrastructure.security;

import com.trade.sales.features.users.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userEntity -> {
                    // Aquí es donde usamos el UserPrincipal que creamos antes
                    // Convertimos la Entity de la BD a un UserDetails que Spring entienda
                    return new UserPrincipal(
                            userEntity.getId(),
                            userEntity.getUsername(),
                            userEntity.getPassword(),
                            userEntity.getRole() // Tu Enum o String de rol
                    );
                })
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Usuario no encontrado: " + username)));
    }
}
