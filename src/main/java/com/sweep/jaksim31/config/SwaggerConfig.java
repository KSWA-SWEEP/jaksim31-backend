package com.sweep.jaksim31.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName :  com.sweep.jaksim31.config
 * fileName : SwaggerConfig
 * author :  방근호
 * date : 2023-01-13
 * description : OpenAPI(Swagger)에 대한 설정, 토큰이 필요한 path와 필요하지 않은 path를 구분할 수 있음
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 * 2023-02-06           방근호             테스트용 URL 추가
 *
 */

@OpenAPIDefinition(
        servers = {@Server(url = "http://localhost:8080"),
                   @Server(url = "https://jaksim31.xyz")},

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
                .group("Jaksim31 Open Api")
                .pathsToMatch("/**")
                .build();
    }
}


