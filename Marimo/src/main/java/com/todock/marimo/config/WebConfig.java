package com.todock.marimo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 여러 경로를 addResourceLocations 메서드에 배열 형태로 전달하여 중복된 경로 핸들러를 하나로 합칩니다.
        registry.addResourceHandler("/data/avatar/**")
                .addResourceLocations(
                        "file:C:/Lecture/project/Marimo-BE/Marimo/data/avatar/", // 아바타 - 메타버스
                        "file:C:/Dev/Project/Mtvs/Marimo-BE/Marimo/data/avatar/", // 아바타 - 노트북
                        "file:C:/Dev/mtvs/Marimo-BE/Marimo/data/avatar/" // 아바타 - 집
                );

        // photo 경로 설정
        registry.addResourceHandler("/data/photo/**")
                .addResourceLocations(
                        "file:C:/Lecture/project/Marimo-BE/Marimo/data/photo/", // 단체사진 - 메타버스
                        "file:C:/Dev/Project/Mtvs/Marimo-BE/Marimo/data/photo/", // 단체사진 - 노트북
                        "file:C:/Dev/mtvs/Marimo-BE/Marimo/data/photo/" // 단체사진 - 집
                );
    }
}
