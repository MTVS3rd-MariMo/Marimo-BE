package com.todock.marimo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/data/avatar/**")
                .addResourceLocations("file:C:/Lecture/project/Marimo-BE/Marimo/data/avatar/");// 메타버스
        //.addResourceLocations("file:C:/Dev/Project/Mtvs/Marimo-BE/Marimo/data/avatar/");// 노트북
        // .addResourceLocations("file:C:/Dev/mtvs/Marimo-BE/Marimo/data/avatar/");// 집
    }
}
