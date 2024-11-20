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
                .info(new Info().title("MARIMO API") // API 제목 설정
                        .description("MARIMO management API documentation") // API 설명
                        .version("v1.0.0") // API 버전
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("MARIMO Project GitHub Documentation")
                        .url("https://github.com/MTVS3rd-MariMo"));
    }

    @Bean
    public GroupedOpenApi avatarGroup() {
        return GroupedOpenApi.builder()
                .group("MARIMO API") // 그룹 이름 설정
                .pathsToMatch("/api/**")
                .build();
    }
}