package com.todock.marimo.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;


@Slf4j // 로깅을 가능하게 한다.
@Aspect // 클래스가 Aspect로서 동작하도록 한다. Aspect : Adivce + pointcut을 모듈화 한 것
@Component // 스프링 빈으로 등록한다.  Spring 컨텍스트에서 동작하기 때문에 빈으로 등록도니 Aspect 클래스가 필요하다.
public class AllLogAdviser {

    /**
     * API 요청 메서드 이름: 어떤 메서드가 호출되었는지 확인한다.
     * 입력 값 (매개변수): 어떤 데이터가 메서드에 전달되었는지 확인한다.
     * 메서드의 반환 값: 메서드가 어떤 결과를 반환하는지 로깅하여 데이터의 흐름을 파악한다.
     * 에러 로그: 메서드 실행 중 에러가 발생했을 경우 에러 메시지를 기록해 문제 파악에 도움을 줌
     * 메서드 실행 시간: 메서드의 시작 시간과 종료 시간을 기록해, 성능을 분석할 수 있다.
     */

    // API 요청 메서드 로그
    @Around("Pointcuts.AllLogPointcut()")
    public Object AdviceMethod(ProceedingJoinPoint proceedingjoinPoint) throws Throwable { // AOP가 적용된 실제 메서드

        long startTime = System.currentTimeMillis(); // 메서드 실행 시작 시간

        MethodSignature methodSignature = (MethodSignature) proceedingjoinPoint.getSignature();
        Method method = methodSignature.getMethod();

        log.info("메서드 호출: {}.{}", method.getDeclaringClass().getName(), method.getName());
        log.info("파라미터 값: {}", Arrays.toString(proceedingjoinPoint.getArgs()));

        Object result = proceedingjoinPoint.proceed(); // 메서드 실행
        long executionTime = System.currentTimeMillis() - startTime; // 메서드 종료 시간

        log.info("메서드 결과: {}", result);
        log.info("메서드 실행 시간: {} ms", executionTime);

        return result;
    }

}





/**
 * ProceedingJoinPoint : Spring AOP에서 @Around 어드바이스에 사용되는 인터페이스. AOP가 적용된 실제 메서드에 대한 정보를 제공
 * JoinPoint : @Before, @After, @AfterReturning, @AfterThrowing 등 메서드 실행 전후에 사용
 * 그 메서드를 실행하거나 실행을 제어할 수 있다.
 * MethodSignature : AOP에서 사용되는 메서드 시그니처를 나타내는 객체, 메서드의 리턴타입, 파라미터 타입, 이름등을 제공
 * Method : Java Reflection API의 일부로, 실제 클래스에서 선언된 메서드에 대한 정보를 나타낸다.
 * Method 객체를 통해 메서드의 이름, 리턴타입, 파라미터 ,어노테이션 등 메서드에 대한 다양한 정보를 조회하고,
 * 동적으로 실행 가능
 * Java Reflection API : 클래스, 메서드, 필드 등의 구조를 런타임 시점에 동적으로 탐색하고 조작할 수 있게 해준다. 이 API를 통해
 * 클래스의 메타데이터에 접근하고, 객체의 상태를 확인하거나 변경하며, 메서드를 호출하는 등의 작업을 한다.
 *
 * @Pointcut: AOP가 적용될 메서드나 클래스의 범위를 설정한다.
 * aspects 앞의 *: Any return type을 넣는자리
 * trace: 패키지
 * *.*: 클래스.메소드
 * (..): Any type and number of arguments가 들어갈 자리
 * execution(* aspects.trace.demo.*.*(..))
 * {} (플레이스 홀더) : SLF4J에서 지원. 로그 메시지를 동적으로 생성할 때 사용
 */