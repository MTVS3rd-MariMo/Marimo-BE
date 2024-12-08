package com.todock.marimo.aspect;

import com.todock.marimo.domain.user.entity.Role;
import com.todock.marimo.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j // 로깅을 가능하게 한다.
@Aspect // 클래스가 Aspect로서 동작하도록 한다. Aspect : Adivce + pointcut을 모듈화 한 것
@Component // 스프링 빈으로 등록한다.  Spring 컨텍스트에서 동작하기 때문에 빈으로 등록도니 Aspect 클래스가 필요하다.
public class UserRoleAspect {

    private UserService userService;

    @Autowired
    public UserRoleAspect(UserService userService) {
        this.userService = userService;
    }


    /**
     * userId가 선생님인지 확인
     */
    @Before("com.todock.marimo.aspect.ExceptionPointCuts.teacherRole()")
    public void isTeacher(JoinPoint joinPoint) throws IllegalAccessException {

        Long userId = getUserId(joinPoint);


        if (Role.TEACHER != userService.findRoleById(userId)) {
            
            log.warn("Access denied for userId : {} 선생님이 아닙니다.", userId);
            throw new IllegalAccessException("선생님이 아닙니다.");
        }
    }


    /**
     * userId가 학생인지 확인
     */
    @Before("com.todock.marimo.aspect.ExceptionPointCuts.studentRole()")
    public void isStudent(JoinPoint joinPoint) throws IllegalAccessException {

        Long userId = getUserId(joinPoint);

        if (Role.STUDENT != userService.findRoleById(userId)) {

            log.warn("Access denied for userId : {} 학생이 아닙니다.", userId);
            throw new IllegalAccessException("학생이 아닙니다.");
        }

    }


    /**
     * userId가 있는지 검증
     */
    private Long getUserId(JoinPoint joinPoint) throws IllegalAccessException {

        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Long) {
            return (Long) args[0];
        }

        throw new IllegalAccessException("userId가 없습니다.");
    }
}