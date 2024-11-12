package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.dto.ParticipantListDto;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.service.LessonService;
import com.todock.marimo.domain.lessonmaterial.dto.ParticipantLessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
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

@RestController
@RequestMapping("/api/lesson")
@Slf4j
@Tag(name = "Lesson API", description = "수업 관련 API")
public class LessonController {

    private final LessonService lessonService;
    private final LessonMaterialService lessonMaterialService;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    @Autowired
    public LessonController(LessonService lessonService, LessonMaterialService lessonMaterialService, LessonRepository lessonRepository) {
        this.lessonService = lessonService;
        this.lessonMaterialService = lessonMaterialService;
    }


    /**
     * 수업 생성 - userId와 lessonMaterialId를 받고 수업 생성 후 LessonId 반환
     */
    @Operation(summary = "수업 생성", description = "userId와 lessonMaterialId를 받고 수업 생성 후 LessonId 반환합니다.")
    @PostMapping("/{lessonMaterialId}")
    public ResponseEntity<Long> createLesson(
            @Parameter(description = "선생님의 사용자 ID", example = "3", required = true) @RequestHeader(value = "userId", required = false) Long userId,
            @Parameter(description = "수업 자료 ID", example = "1", required = true) @PathVariable("lessonMaterialId") Long lessonMaterialId) {

        log.info("수업에 진행할 lessonMaterialId: {}", lessonMaterialId);

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
        log.info("참가자 Id = {}", participantListDto);

        return ResponseEntity.ok(participantListDto);
    }


    /**
     * 수업 중  lessonId로 수업용 수업자료 상세 조회
     */
    @Operation(summary = "학생용 수업 자료 조회", description = "수업 중 학생이 조회할 수업 자료의 상세 정보를 반환합니다.")
    @GetMapping("/{lessonId}")
    public ResponseEntity<ParticipantLessonMaterialDto> getLessonMaterial(
            @Parameter(description = "조회하려는 수업 자료 ID", example = "101", required = true) @PathVariable("lessonId") Long lessonId) {

        log.info("getLessonId: {}", lessonId);

        ParticipantLessonMaterialDto participantLessonMaterialDto = lessonMaterialService.getLessonMaterialById(lessonId);

        return ResponseEntity.ok(participantLessonMaterialDto);
    }


    /**
     * 단체사진 저장
     */
    @PostMapping("/photo/{lessonId}")
    public ResponseEntity<String> updatePhoto(
            @PathVariable("lessonId") Long lessonId
            , @RequestParam("img") MultipartFile photo) {
        try {
            // 1. 파일 존재 여부 검증
            if (photo == null || photo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }

            // 2. 파일 크기 검증
            if (photo.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }


            String originalFilename = photo.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            // 3. 파일 확장자 검증
            if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        // .body("허용되지 않는 파일 형식입니다. jpg, jpeg, png 파일만 업로드 가능합니다.");
                        .body(null);
            }

            // 4. 파일 내용 검증
            try {
                BufferedImage bufferedImage = ImageIO.read(photo.getInputStream());
                if (bufferedImage == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            //.body("유효하지 않은 이미지 파일입니다.");
                            .body(null);
                }
            } catch (IOException e) {
                log.error("이미지 파일 검증 중 오류 발생", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("이미지 파일 검증 중 오류가 발생했습니다.");
                        .body(null);
            }

            // 5. 서비스 호출
            lessonService.savePhoto(lessonId, photo);

            return ResponseEntity.ok("사진이 저장되었습니다.");

        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 배경사진 제작 요청 - AI서버에 pdf 텍스트를 보내서 단체사진 배경 제작 요청
     */
    @PutMapping("/photo/background/{lessonId}")
    public ResponseEntity<String> getBackGround(
            @PathVariable("lessonId") Long lessonId) {

        log.info("요청한 수업 번호: {}", lessonId);

        lessonService.createBackground(lessonId);

        return ResponseEntity.ok("사진관 배경을 생성했습니다.");
    }


    /**
     * 배경사진 호출
     */
    @GetMapping("/photo/background/{lessonId}")
    public ResponseEntity<String> getPhotoBackground(
            @PathVariable("lessonId") Long lessonId) {

        return ResponseEntity.ok().body(lessonService.getPhotoBackgroundUrl(lessonId));
    }


    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}