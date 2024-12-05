package com.todock.marimo.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class PointCuts {

    /**
     * teacherRole() 포인트컷
     */
    @Pointcut("execution(* com.todock.marimo.domain..teacher*(.., Long))")  // Long 타입의 userId를 받는 메서드
    public void teacherRole() {
    }

    /**
     * studentRole() 포인트컷
     */
    @Pointcut("execution(* com.todock.marimo.domain..student*(.., Long))")  // Long 타입의 userId를 받는 메서드
    public void studentRole() {
    }
}
