package com.phoenix_sat.phoenix_sat_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfiguration {
//    private final SwaggerConstants swaggerConstants;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(getServers())
                .components(
                        new Components()
                                .addSecuritySchemes("bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer").bearerFormat("jti"))
                )
                .security(List.of(new SecurityRequirement().addList("bearerAuth")))
                .info(new Info().title("Phoenix Backend").version("V1"));
    }

    public List<Server> getServers() {
        List<Server> servers = new LinkedList<>();
//        servers.add(new Server().url(swaggerConstants.getBaseUrl()));
        servers.add(new Server().url("http://35.232.217.186"));
        servers.add(new Server().url("http://localhost:8080"));
        return servers;
    }
}
