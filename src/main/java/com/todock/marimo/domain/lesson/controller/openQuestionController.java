package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.dto.AnswerRequestDto;
import com.todock.marimo.domain.lesson.service.OpenQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/open-question")
@Tag(name = "OpenQuestion API", description = "열린 질문 관련 API")
public class openQuestionController {

    private final OpenQuestionService openQuestionService;

    @Autowired
    public openQuestionController(OpenQuestionService openQuestionService) {
        this.openQuestionService = openQuestionService;
    }


    /**
     * 열린 질문 개인 답변 저장
     */
    @Operation(summary = "열린 질문 개인 답변 저장")
    @PutMapping()
    public ResponseEntity<String> StudentOpenQuestion(
            @RequestHeader("userId") Long userId
            , @RequestBody AnswerRequestDto answerDto) {

        log.info("열린 질문에 대한 답변 을저장 하는 유저의 userId : {}와 Dto : {}", userId, answerDto);

        openQuestionService.saveAnswer(userId, answerDto);

        return ResponseEntity.ok("열린 질문 활동이 저장되었습니다.");
    }
}
