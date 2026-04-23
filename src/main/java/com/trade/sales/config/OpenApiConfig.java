package com.trade.sales.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trade Sales API")
                        .version("1.0")
                        .description("Sistema de ventas genérico (Zapatillas, Tortas, etc.)"))
                // 1. Esto hace que los endpoints tengan el candadito
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                // 2. Esto define qué tipo de seguridad usas (JWT)
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

//    @Bean
//    public GroupedOpenApi businessApi() {
//        return GroupedOpenApi.builder()
//                .group("1 - Operaciones de Negocio") // Sale primero por el "1"
//                .pathsToMatch("/api/v1/**")         // Solo tus controllers
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi monitoringApi() {
//        return GroupedOpenApi.builder()
//                .group("2 - Monitoreo y Sistema")   // Sale segundo
//                .pathsToMatch("/actuator/**")       // Solo Actuator
//                .build();
//    }
}