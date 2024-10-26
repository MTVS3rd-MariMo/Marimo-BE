package com.todock.marimo.logging;

import org.aspectj.lang.annotation.Pointcut;


public class Pointcuts {

    /**
     * 도메인 전체 범위 Pointcut && !auth
     */
    @Pointcut("execution(* com.yfmf.footlog.domain..*(..)) && !execution(* com.yfmf.footlog.domain.auth..*(..))")
    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
    public void AllLogPointcut() {
    }

    /**
     * club 전체 범위 Pointcut
     */
    @Pointcut("execution(* com.yfmf.footlog.domain.club..*(..))")
    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
    public void clubLogPointcut() {
    }

    /**
     * guest 도메인 전체 범위 Pointcut
     */
    @Pointcut("execution(* com.yfmf.footlog.domain.guest..*(..))")
    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
    public void guestLogPointcut() {
    }

    /**
     * match 도메인 전체 범위 Pointcut
     */
    @Pointcut("execution(* com.yfmf.footlog.domain.match..*(..))")
    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
    public void matchLogPointcut() {
    }

    /**
     * member 도메인 전체 범위 Pointcut
     */
    @Pointcut("execution(* com.yfmf.footlog.domain.member..*(..))")
    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
    public void memberLogPointcut() {
    }

}
