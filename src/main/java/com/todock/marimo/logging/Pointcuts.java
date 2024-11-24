package com.todock.marimo.logging;

import org.aspectj.lang.annotation.Pointcut;


public class Pointcuts {

    /**
     * 도메인 전체 범위 Pointcut && !auth
     */
    @Pointcut("execution(* com.todock.marimo..controller..*(..))")
    // 여기서 hello.springmvc.basic 패키지와 그 하위 패키지에 있는 모든 메서드에 AOP를 적용한다
    public void ControllerLogPointcut() {
    }

    /**
     * AI 서버 요청 시간 확인
     */
    @Pointcut(
            "execution(* com.todock.marimo.domain.lesson.service.PhotoService.createBackground(..)) || " +
                    "execution(* com.todock.marimo.domain.lesson.service.AvatarService.sendImgToAiServer(..)) || " +
                    "execution(* com.todock.marimo.domain.lesson.service.HotSittingService.sendWavToAiServer(..)) || " +
                    "execution(* com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService.sendPdfToAiServer(..))"
    )
    public void aiServerPointCut() {
    }

}
