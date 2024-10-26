package com.todock.marimo.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Slf4j // 로깅을 가능하게 한다.
@Aspect // 클래스가 Aspect로서 동작하도록 한다. Aspect : Adivce + pointcut을 모듈화 한 것
@Component // 스프링 빈으로 등록한다.  Spring 컨텍스트에서 동작하기 때문에 빈으로 등록도니 Aspect 클래스가 필요하다.
public class LoggingAspect {

    /**
     * @Pointcut: AOP가 적용될 메서드나 클래스의 범위를 설정한다.
     * <p>
     * execution : 메서드 실행 시점
     * * : 리턴 타입
     * com.yfmf.footlog.* : 해당 패키지의 모든 파일
     * *(..)) : 모든 메서드
     */

    @Pointcut("execution(* com.yfmf.footlog.domain..*(..))")
    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
    private void cut() {
        // 로직 필요없음. 다른 곳에 재사용하기 편하도록 선언한 것 뿐이다.
    }

    @Around("cut()")
    public Object AdviceMethodName(ProceedingJoinPoint joinPoint) throws Throwable { // AOP가 적용된 실제 메서드
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 메서드 이름 로깅
        log.info("메서드 실행: {}", method.getName());

        // 실제 메서드 실행
        Object result = joinPoint.proceed(); // proceed() : 메서드를 실행하는 역할

        // 리턴값 로깅
        log.info("메서드 리턴값: {}", result);

        // 리턴값이 null이 아닌 경우에만 타입을 로깅
        if (result != null) {
            log.info("return type = {}", result.getClass().getSimpleName());
        } else {
            log.info("메서드 리턴값이 null입니다.");
        }

        // 원래 메서드의 리턴값 반환
        return result;
    }


    /**
     * ProceedingJoinPoint : Spring AOP에서 @Around 어드바이스에 사용되는 인터페이스. AOP가 적용된 실제 메서드에 대한 정보를 제공
     *                       그 메서드를 실행하거나 실행을 제어할 수 있다.
     * MethodSignature : AOP에서 사용되는 메서드 시그니처를 나타내는 객체, 메서드의 리턴타입, 파라미터 타입, 이름등을 제공
     * Method : Java Reflection API의 일부로, 실제 클래스에서 선언된 메서드에 대한 정보를 나타낸다.
     *          Method 객체를 통해 메서드의 이름, 리턴타입, 파라미터 ,어노테이션 등 메서드에 대한 다양한 정보를 조회하고,
     *          동적으로 실행 가능
     * Java Reflection API : 클래스, 메서드, 필드 등의 구조를 런타임 시점에 동적으로 탐색하고 조작할 수 있게 해준다. 이 API를 통해
     *                       클래스의 메타데이터에 접근하고, 객체의 상태를 확인하거나 변경하며, 메서드를 호출하는 등의 작업을 한다.
     */


    /**
     * @Around(): 메서드 실행 전후에 특정 로직을 실행할 수 있게 해준다.
     */

    /*@Around("cut()") // pointcut으로 정의한 메서드를 참조
    public Object aroundLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // ProceedingJoinPoint : AOP가 적용된 실제 메서드에 대한 정보를 제공하고, 그 메서드를 실행할 수 있는 객체
        // 이 객체를 통해 메서드 이름, 파라미터, 리턴 값 등 다양한 정보를 얻을 수 있고, 메서드 실행 자체도 proceed()를 통해 제어할 수 있다


        // Method : Java Reflection API의 일부로, 클래스의 메서드에 대한 정보를 나타내는 객체를 제공한다.
        Method method = getMethod(proceedingJoinPoint);

        *//** 메서드 이름 받아오기*//*
        log.info("====== method name = {}", method.getName());


        *//** 파라미터 받아오기 *//*
        // proceedingJoinPoint.getArgs(): AOP가 적용도니 메서드에 전달된 파라미터(메서드 정보)들을 배열로 가져온다.
        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length == 0) log.info("no parameter"); // 파라미터가 없을 때 0일때 로그
        for (Object arg : args) { // 파라미터가 있을 때,
            log.info("parameter type = {}", arg.getClass().getSimpleName());
            log.info("parameter value = {}", arg);

        }

        // proceed()를 호출하여 실제 메서드 실행
        Object returnObj = proceedingJoinPoint.proceed();

        // 메서드의 리턴값 로깅
        log.info("return type = {}", returnObj.getClass().getSimpleName());
        log.info("return value = {}", returnObj);

        return returnObj;
    }*/

    /**
     * getMethod() : ProceedingJoinPoint 객체로부터 실행된 메서드의 정보를 가져온다.
     */
    private Method getMethod(ProceedingJoinPoint proceedingJoinPoint) {

        // MethodSignature : AOP에서 사용되는 Signature의 서브 인터페이스, 메서드에 대한 구체적인 정보를 제공하는 객체
        //          -> 메서드 이름, 리턴 타입, 파라미터 타입, 메서드 객체

        // getSignature(): 실행 중인 메서드의 서명(signature)을 반환한다. 서명에는 위와 같이 다양한 정보가 담겨있다.
        // 캐스팅 하는 이유 : Signature는 다양한 서명을 포함할 수 있지만, 
        //                 서명을 메서드에 대한 더 구체적인 정보를 제공하는 MethodSignature로 변환합니다.
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();

        // MethodSignature의 객체를 사용해서 실제 실행된 메서드에 대한 정보를 가져온다.
        return signature.getMethod();
    }
}
