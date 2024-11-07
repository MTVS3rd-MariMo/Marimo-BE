package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.dto.ParticipantListDto;
import com.todock.marimo.domain.lesson.service.LessonService;
import com.todock.marimo.domain.lessonmaterial.dto.StudentLessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.dto.TeacherLessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lesson")
@Slf4j
@Tag(name = "Lesson API", description = "수업 관련 API")
public class LessonController {

    private final LessonService lessonService;
    private final LessonMaterialService lessonMaterialService;

    @Autowired
    public LessonController(LessonService lessonService, LessonMaterialService lessonMaterialService) {
        this.lessonService = lessonService;
        this.lessonMaterialService = lessonMaterialService;
    }

    /**
     * 수업 생성 - lessonMaterialId를 받고 수업자료와 LessonId와 LessonMaterial 반환
     */
    @Operation(summary = "수업 생성", description = "lessonMaterialId를 받고 새로운 수업을 생성하여 반환합니다.")
    @PostMapping("/{lessonMaterialId}")
    public ResponseEntity<Long> createLesson(
            @Parameter(description = "선생님의 사용자 ID", example = "3", required = true) @RequestHeader(value = "userId", required = false) Long userId,
            @Parameter(description = "수업 자료 ID", example = "1", required = true) @PathVariable("lessonMaterialId") Long lessonMaterialId) {

        log.info("Creating lesson by lessonMaterialId: {}", lessonMaterialId);
        log.info("Lesson ID: {}", lessonMaterialId);

        Long lessonId = lessonService.createLesson(userId, lessonMaterialId);

        return ResponseEntity.ok(lessonId);
    }

    /**
     * LessonId로 participant 목록에 userId, userName 추가하기
     */
    @Operation(summary = "수업에 참가", description = "주어진 LessonId로 유저를 수업에 참가시킵니다.")
    @PutMapping("/enter/{lessonId}")
    public ResponseEntity<String> enter(
            @Parameter(description = "참가하려는 사용자 ID", example = "1", required = true) @RequestHeader(value = "userId", required = false) Long userId,
            @Parameter(description = "참가하려는 수업 ID", example = "101", required = true) @PathVariable("lessonId") Long lessonId) {

        if (userId == null) {
            return ResponseEntity.badRequest().body("userId 헤더가 필요합니다.");
        }

        log.info("userId = {}, lessonId = {}", userId, lessonId);

        lessonService.updateUserByLessonId(userId, lessonId);

        return ResponseEntity.ok("유저 " + userId + "가 " + lessonId + "에 참가하였습니다.");
    }

    /**
     * 참가자들이 participant 목록 서버에 요청
     */
    @Operation(summary = "수업 참가자 목록 조회", description = "주어진 LessonId의 참가자 목록을 조회합니다.")
    @GetMapping("/participant/{lessonId}")
    public ResponseEntity<ParticipantListDto> getStudentLessonMaterial(
            @Parameter(description = "조회하려는 수업 ID", example = "10", required = true) @PathVariable("lessonId") Long lessonId) {

        ParticipantListDto participantListDto = lessonService.findParticipantByLessonId(lessonId);

        return ResponseEntity.ok(participantListDto);
    }

    /**
     * 수업 중 학생용 lessonMaterialId로 수업용 수업자료 상세 조회
     */
    @Operation(summary = "학생용 수업 자료 조회", description = "수업 중 학생이 조회할 수업 자료의 상세 정보를 반환합니다.")
    @GetMapping("/{lessonId}")
    public ResponseEntity<StudentLessonMaterialDto> getLessonMaterial(
            @Parameter(description = "조회하려는 수업 자료 ID", example = "101", required = true) @PathVariable("lessonId") Long lessonId) {

        log.info("getLessonId: {}", lessonId);

        StudentLessonMaterialDto studentLessonMaterialDto = lessonMaterialService.getLessonMaterialById(lessonId);

        return ResponseEntity.ok(studentLessonMaterialDto);
    }

}