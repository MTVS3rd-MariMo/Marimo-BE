package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.service.LessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lesson")
@Slf4j
public class LessonController {

    private final LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }


    /**
     * 수업 생성
     * (lessonId, 참가자(유저 id...), 수업 자료(퀴즈, 열린질문, 핫시팅, 사진)
     */


    /**
     * 유저에게 lessonId를 받으면 lessonId를 이용해서 lesson을 찾고 participant에 유저를 등록한다.
     */
    @PutMapping("/enter")
    public ResponseEntity<String> enter(@RequestHeader Long userId, @RequestBody Long lessonId) {

        log.info("userId = {}, lessonId = {}", userId, lessonId);

        lessonService.updateUserByLessonId(userId, lessonId);

        return ResponseEntity.ok("유저" + userId + "가 " + lessonId + "에 참가하였습니다.");
    }

}
