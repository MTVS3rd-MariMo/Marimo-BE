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
            //@RequestHeader("userId") Long userId
            @PathVariable("lessonId") Long lessonId) {

        log.info("참가자가 lessonId : {}로 수업의 참가자 목록을 요청", lessonId);
        
        ParticipantListDto participantListDto = lessonService.findParticipantByLessonId(lessonId);
        log.info("참가자 Id = {}", participantListDto);

        return ResponseEntity.ok(participantListDto);
    }


    /**
     * 수업 중  lessonId로 수업용 수업자료 상세 조회
     */
    @Operation(summary = "참가자 수업 자료 조회", description = "수업 중 참가자가 사용할 수업 자료의 상세 정보를 반환합니다.")
    @GetMapping("/{lessonId}")
    public ResponseEntity<ParticipantLessonMaterialDto> getLessonMaterial(
            @PathVariable("lessonId") Long lessonId) {

        log.info("참가자가 lessonId: {}로 수업에 사용하는 수업자료 조회", lessonId);

        ParticipantLessonMaterialDto participantLessonMaterialDto = lessonMaterialService.getLessonMaterialById(lessonId);

        return ResponseEntity.ok(participantLessonMaterialDto);
    }


    /**
     * 단체사진 저장
     */
    @Operation(summary = "단체사진 저장")
    @PostMapping("/photo/{lessonId}")
    public ResponseEntity<String> updatePhoto(
            @PathVariable("lessonId") Long lessonId
            , @RequestParam("img") MultipartFile photo) {

        log.info("단체사진 저장할 수업의 lessonId : {} 와 img: {}", lessonId, photo);

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
    @Operation(summary = "배경사진 제작 요청")
    @PostMapping("/photo/background/{lessonId}")
    public ResponseEntity<String> getBackGround(
            @PathVariable("lessonId") Long lessonId) {

        log.info("배경사진 제작 요청 할 수업의 lessonId: {}", lessonId);

        lessonService.createBackground(lessonId);

        return ResponseEntity.ok("사진관 배경을 생성했습니다.");
    }


    /**
     * 배경사진 호출
     */
    @Operation(summary = "배경사진 호출")
    @GetMapping("/photo/background/{lessonId}")
    public ResponseEntity<String> getPhotoBackground(
            @PathVariable("lessonId") Long lessonId) {

        log.info("배경 사진을 가져오기 위한 현재 lessonId: {}", lessonId);

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