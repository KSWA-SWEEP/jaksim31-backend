package com.sweep.jaksim31.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Jaksim31 API",
                description = "일기관리 서비스 \"작심상일\" API 명세서",
                version = "v1"))

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi NonSecurityGroupOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("Non Security Open Api")
                .pathsToMatch("/v0/**")
                .build();
    }

    @Bean
    public GroupedOpenApi SecurityGroupOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("Security Open Api")
                .pathsToExclude("/v1/**", "/v2/**")
                .addOpenApiCustomiser(buildSecurityOpenApi())
                .build();
    }

    public OpenApiCustomiser buildSecurityOpenApi() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .bearerFormat("JWT")
                .scheme("bearer");

        return OpenApi -> OpenApi
                .addSecurityItem(new SecurityRequirement().addList("jwt token"))
                .getComponents().addSecuritySchemes("jwt token", securityScheme);
    }
}


