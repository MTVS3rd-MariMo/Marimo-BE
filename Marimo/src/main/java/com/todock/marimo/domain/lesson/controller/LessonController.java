package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.dto.LessonOpenQuestionRequestDto;
import com.todock.marimo.domain.lesson.dto.ParticipantListDto;
import com.todock.marimo.domain.lesson.service.LessonService;
import com.todock.marimo.domain.lessonmaterial.dto.StudentLessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.dto.TeacherLessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lesson")
@Slf4j
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
    @PostMapping
    public ResponseEntity<TeacherLessonMaterialDto> createLesson(
            @RequestHeader Long userId, @RequestBody Long lessonMaterialId) {

        log.info("Creating lesson by lessonMaterialId: {}", lessonMaterialId);

        TeacherLessonMaterialDto teacherLessonMaterialDto = lessonService.createLesson(userId, lessonMaterialId);

        return ResponseEntity.ok(teacherLessonMaterialDto); // LessonMaterial, lessonId 반환


    }


    /**
     * LessonId로 participant 목록에 userId, userName 추가하기
     */
    @PutMapping("/enter")
    public ResponseEntity<String> enter(@RequestHeader Long userId, @RequestBody Long lessonId) {

        log.info("userId = {}, lessonId = {}", userId, lessonId);

        lessonService.updateUserByLessonId(userId, lessonId);

        return ResponseEntity.ok("유저" + userId + "가 " + lessonId + "에 참가하였습니다.");
    }


    /**
     * 참가자들이 participant 목록 서버에 요청
     */
    @GetMapping("/participant/{lessonId}")
    public ResponseEntity<ParticipantListDto> getStudentLessonMaterial(@PathVariable Long lessonId) {

        ParticipantListDto participantListDto = lessonService.findParticipantByLessonId(lessonId);

        return ResponseEntity.ok(participantListDto);
    }


    /**
     * 수업 중 학생용 lessonMaterialId로 수업용 수업자료 상세 조회
     */
    @GetMapping("/{lessonMaterialId}")
    public ResponseEntity<StudentLessonMaterialDto> getLessonMaterial(@PathVariable Long lessonMaterialId) {
        log.info("getLessonMaterial: {}", lessonMaterialId);

        StudentLessonMaterialDto studentLessonMaterialDto = lessonMaterialService.getLessonMaterialById(lessonMaterialId);

        return ResponseEntity.ok(studentLessonMaterialDto); // LessonMaterial, lessonId 반환
    }

}
