package com.todock.marimo.domain.result.controller;

import com.todock.marimo.domain.result.dto.*;
import com.todock.marimo.domain.result.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/result")
@Tag(name = "Result API", description = "수업결과 관련 API")
public class ResultController {

    private final ResultService resultService;

    @Autowired
    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }


    /**
     * 학생이 참가한 모든 수업 리스트 조회
     */
    @Operation(summary = "userId로 참가한 모든 수업 조회 - 학생")
    @GetMapping("/student")
    public ResponseEntity<StudentResultResponseDto> studentGetPhotoList(
            @RequestHeader("userId") Long userId) {

        return ResponseEntity.ok().body(resultService.findAllPhotos(userId));
    }


    /**
     * 선생님이 참가한 모든 수업 조회
     */
    @Operation(summary = "userId로 참가한 모든 수업 조회 - 선생님")
    @GetMapping("/teacher")
    public ResponseEntity<TeacherResultResponseDto> teacherGetLessonList(
            @RequestHeader("userId") Long userId) {

        return ResponseEntity.ok().body(resultService.findAllLessons(userId));
    }


    /**
     * 선생님이 참가한 수업 상세 조회
     */
    @Operation(summary = "선생님 수업 결과 조회")
    @GetMapping("/teacher/{lessonId}")
    public ResponseEntity<LessonResultDto> teacherGetLesson(
            @RequestHeader("userId") Long userId,
            @PathVariable("lessonId") Long lessonId) {

        return ResponseEntity.ok().body(resultService.lessonDetail(lessonId));
    }

}
