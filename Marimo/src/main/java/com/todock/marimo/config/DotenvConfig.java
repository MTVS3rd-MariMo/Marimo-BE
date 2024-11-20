package com.todock.marimo.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadEnv() {
        // .env 파일 로드
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // 프로젝트 루트 디렉토리에서 .env 검색
                .ignoreIfMalformed() // 잘못된 포맷이 있어도 무시
                .ignoreIfMissing()   // .env 파일이 없어도 무시
                .load();

        // .env 파일의 모든 변수를 시스템 속성으로 설정
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}