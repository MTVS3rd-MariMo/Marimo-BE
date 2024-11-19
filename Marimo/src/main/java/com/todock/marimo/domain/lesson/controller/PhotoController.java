package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/photo")
@Tag(name = "Photo API", description = "사진 관련 API")
public class PhotoController {

    private final PhotoService photoService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }


    /**
     * 배경사진 제작 요청 - AI서버에 pdf 텍스트를 보내서 단체사진 배경 제작 요청
     */
    @Operation(summary = "배경사진 제작 요청")
    @PostMapping("/background/{lessonMaterialId}")
    public ResponseEntity<String> getBackGround(
            @PathVariable("lessonMaterialId") Long lessonMaterialId) {

        log.info("배경사진 제작 요청 할 lessonMaterialId: {}", lessonMaterialId);

        photoService.createBackground(lessonMaterialId);

        return ResponseEntity.ok(photoService.createBackground(lessonMaterialId));
    }


    /**
     * AI서버에서 서버에 배경사진 저장
     */
    @Operation(summary = "배경사진 저장")
    @PostMapping("/background/ai/{lessonMaterialId}")
    public ResponseEntity<String> saveBackgroundImg(
            @PathVariable("lessonMaterialId") Long lessonMaterialId,
            @RequestParam("img") MultipartFile img) {

        log.info("배경 사진을 가져오기 위한 현재 lessonMaterialId: {}", lessonMaterialId);

        return ResponseEntity.ok().body(photoService.saveBackground(lessonMaterialId, img));
    }


    /**
     * 배경사진 호출 - 수업 자료 호출시점에 같이 반환 (삭제 필요)
     */
    @Operation(summary = "배경사진 호출")
    @GetMapping("/background/{lessonId}")
    public ResponseEntity<String> getPhotoBackground(
            @PathVariable("lessonId") Long lessonId) {

        log.info("배경 사진을 가져오기 위한 현재 lessonId: {}", lessonId);

        return ResponseEntity.ok().body(photoService.getPhotoBackgroundUrl(lessonId));
    }


    /**
     * 단체사진 저장
     */
    @Operation(summary = "단체사진 저장")
    @PostMapping("/{lessonId}")
    public ResponseEntity<String> saveGroupPhoto(
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
            photoService.savePhoto(lessonId, photo);

            return ResponseEntity.ok("사진이 저장되었습니다.");

        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 처리 중 오류가 발생했습니다.");
        }
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
