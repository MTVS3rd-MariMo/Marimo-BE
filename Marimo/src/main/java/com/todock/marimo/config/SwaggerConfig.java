package com.todock.marimo.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Bean 설정
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info().title("Avatar API") // API 제목 설정
                        .description("Avatar management API documentation")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Avatar Project Documentation")
                        .url("https://example.com/docs"));
    }

    @Bean
    public GroupedOpenApi avatarGroup() {
        return GroupedOpenApi.builder()
                .group("avatar")
                .pathsToMatch("/api/avatar/**")
                .build();
    }
}