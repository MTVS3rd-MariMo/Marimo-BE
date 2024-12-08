package com.todock.marimo.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j // 로깅 활성화
@Aspect // 이 클래스를 Advice와 Pointcut을 포함한 Aspect로 설정
@Component // 이 클래스를 Spring Bean으로 등록
public class AllLogAdviser {

    /**
     * API 메서드의 세부 정보를 로깅합니다:
     * - 메서드 이름: 호출된 메서드의 이름을 확인합니다.
     * - 실행 시간: 시작 및 종료 시간을 기록하여 성능을 분석합니다.
     */


    @Around("com.todock.marimo.logging.LoggingPointcuts.ControllerLogPointcut()") // 컨트롤러 중 AI 메서드 제외
    // @Around("Pointcuts.ControllerLogPointcut()") // 컨트롤러 중 AI 메서드 제외
    public Object logMethodDetails(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = null;

        try {
            // 메서드 시그니처 및 메서드 정보 가져오기
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            methodName = method.getDeclaringClass().getSimpleName() + " - " + method.getName();

            // 메서드 이름 로깅
            log.info("🎯 호출된 컨트롤러 메서드: {}", methodName);

            // 메서드 실행
            Object result = joinPoint.proceed();

            return result; // 실행 결과 반환

        } catch (Throwable e) {

            log.error("❌ 컨트롤러 메서드 실행 중 에러: {} | 에러 메시지: {}", methodName, e.getMessage(), e);
            throw e; // 예외 재발생
        }
    }

}
