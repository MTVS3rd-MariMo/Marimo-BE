package com.todock.marimo.domain.lessonresult.controller;

import com.todock.marimo.domain.lesson.dto.LessonOpenQuestionRequestDto;
import com.todock.marimo.domain.lessonresult.repository.LessonResultRepository;
import com.todock.marimo.domain.lessonresult.service.LessonResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/lesson-result")
public class LessonResultController {

    private final LessonResultService lessonResultService;

    @Autowired
    public LessonResultController(LessonResultService lessonResultService) {
        this.lessonResultService = lessonResultService;
    }

    /**
     * 열린질문 결과 저장
     */
    @PutMapping("/open-question")
    public ResponseEntity<String> openQuestion(@RequestBody LessonOpenQuestionRequestDto lessonOpenQuestionRequestDto) {

        log.info("openQuestion: {}", lessonOpenQuestionRequestDto);

        lessonResultService.updateLessonResult(lessonOpenQuestionRequestDto);

        return ResponseEntity.ok("열린 질문 결과를 저장했습니다.");
    }
}
