package com.todock.marimo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // RestTemplate은 Spring에서 외부 서버와 HTTP 요청을 수행하는 데 사용됩니다. 이를 빈으로 등록해 재사용할 수 있게 설정합니다.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}