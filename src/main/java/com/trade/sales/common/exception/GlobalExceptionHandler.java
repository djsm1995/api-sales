package com.trade.sales.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleBusinessException(BusinessException ex, ServerWebExchange exchange) {
        log.error("Error de negocio: {}", ex.getMessage());

        ErrorMessage error = ErrorMessage.builder()
                .message(ex.getMessage())
                .code(ex.getStatus().value())
                .status(ex.getStatus().name())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();

        return Mono.just(ResponseEntity.status(ex.getStatus()).body(error));
    }

    // Captura errores inesperados (NullPointer, etc.)
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorMessage>> handleGeneralException(Exception ex, ServerWebExchange exchange) {
        log.error("Error no controlado: ", ex);

        ErrorMessage error = ErrorMessage.builder()
                .message("Ocurrió un error interno inesperado")
                .code(500)
                .status("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();

        return Mono.just(ResponseEntity.status(500).body(error));
    }

}
