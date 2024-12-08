FROM eclipse-temurin:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

# CMD ["java", "-jar", "/app.jar"]

# Nginx 설치를 위해 별도 명령 실행
RUN apt-get update && \
    apt-get install -y nginx && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Nginx 설정 파일을 컨테이너로 복사
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80 9999

# Nginx와 애플리케이션 모두 실행
CMD ["sh", "-c", "service nginx start && java -jar /app.jar"]