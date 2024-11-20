package com.todock.marimo.logging;

import org.aspectj.lang.annotation.Pointcut;


public class Pointcuts {

    /**
     * 도메인 전체 범위 Pointcut && !auth
     */
    @Pointcut("execution(* com.todock.marimo.domain..*(..)) && !execution(* com.todock.marimo.domain..*Repository.*(..))")
    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
    public void AllLogPointcut() {
    }

    /**
     * club 전체 범위 Pointcut
     */
//    @Pointcut("execution(* com.yfmf.footlog.domain.club..*(..))")
//    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
//    public void clubLogPointcut() {
//    }


}
