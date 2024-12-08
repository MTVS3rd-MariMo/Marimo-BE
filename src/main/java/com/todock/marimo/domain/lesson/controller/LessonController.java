package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.dto.ParticipantListDto;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.service.LessonService;
import com.todock.marimo.domain.lessonmaterial.dto.ParticipantLessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lesson")
@Tag(name = "Lesson API", description = "수업 관련 API")
public class LessonController {

    private final LessonService lessonService;
    private final LessonMaterialService lessonMaterialService;


    @Autowired
    public LessonController(
            LessonMaterialService lessonMaterialService,
            LessonService lessonService) {
        this.lessonMaterialService = lessonMaterialService;
        this.lessonService = lessonService;
    }


    /**
     * 수업 생성 - userId와 lessonMaterialId를 받고 수업 생성 후 LessonId 반환
     */
    @Operation(summary = "수업 생성", description = "userId와 lessonMaterialId를 받고 수업 생성 후 LessonId 반환합니다.")
    @PostMapping("/{lessonMaterialId}")
    public ResponseEntity<Long> teacherCreateLesson(
            @RequestHeader(value = "userId", required = false) Long userId
            , @PathVariable("lessonMaterialId") Long lessonMaterialId) {

        log.info("수업에 사용할 수업자료를 lessonMaterialId: {}로 전달 후 수업 생성", lessonMaterialId);

        Long lessonId = lessonService.createLesson(userId, lessonMaterialId);

        return ResponseEntity.ok(lessonId);
    }


    /**
     * LessonId로 participant 목록에 userId, userName 추가하기
     */
    @Operation(summary = "수업 참가자 목록에 유저 추가", description = "userId와 lessonMaterialId를 받고 수업 생성 후 LessonId 반환합니다.")
    @PutMapping("/enter/{lessonId}")
    public ResponseEntity<String> enter(
            @RequestHeader(value = "userId", required = false) Long userId
            , @PathVariable("lessonId") Long lessonId) {

        log.info("수업 참가자가 수업 참가자 목록에 lessonId: {}로 조회 후 추가", lessonId);

        if (userId == null) {
            return ResponseEntity.badRequest().body("userId 헤더가 필요합니다.");
        }

        lessonService.updateUserByLessonId(userId, lessonId);

        return ResponseEntity.ok("유저 " + userId + "가 " + lessonId + "에 참가하였습니다.");
    }


    /**
     * 참가자들이 participant 목록 서버에 요청
     */
    @Operation(summary = "수업 참가자 목록 조회", description = "주어진 LessonId의 참가자 목록을 조회합니다.")
    @GetMapping("/participant/{lessonId}")
    public ResponseEntity<ParticipantListDto> getStudentLessonMaterial(
            @RequestHeader("userId") Long userId,
            @PathVariable("lessonId") Long lessonId) {

        log.info("참가자가 lessonId : {}로 수업의 참가자 목록을 요청", lessonId);

        ParticipantListDto participantListDto = lessonService.findParticipantByLessonId(lessonId);
        log.info("참가자 Id = {}", participantListDto);

        return ResponseEntity.ok(participantListDto);
    }


    /**
     * 참가자들이 수업에 사용하는 수업자료를 lessonMaterialId로 요청
     */
    @Operation(summary = "참가자 수업 자료 조회", description = "수업 중 참가자가 사용할 수업 자료의 상세 정보를 반환합니다.")
    @GetMapping("/{lessonMaterialId}")
    public ResponseEntity<ParticipantLessonMaterialDto> getLessonMaterial(
            @RequestHeader("userId") Long userId,
            @PathVariable(name = "lessonMaterialId") Long lessonMaterialId) {

        log.info("참가자가 lessonMaterialId: {}로 수업에 사용하는 수업자료 조회", lessonMaterialId);

        ParticipantLessonMaterialDto participantLessonMaterialDto = lessonService.getLessonMaterialById(lessonMaterialId);

        return ResponseEntity.ok(participantLessonMaterialDto);
    }

}