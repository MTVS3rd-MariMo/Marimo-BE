package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.dto.AvatarResponseDto;
import com.todock.marimo.domain.lesson.service.AvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/avatar")
@Tag(name = "Avatar API", description = "아바타 관련 API")
public class AvatarController {

    private final AvatarService avatarService;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private final HttpEncodingAutoConfiguration httpEncodingAutoConfiguration;

    @Autowired
    public AvatarController(AvatarService avatarService, HttpEncodingAutoConfiguration httpEncodingAutoConfiguration) {
        this.avatarService = avatarService;
        this.httpEncodingAutoConfiguration = httpEncodingAutoConfiguration;
    }

    /**
     * 모든 유저의 아바타와 이미지를 한번에 List<dto>로 전달 - 선생님용?
     * 수업 id로 아바타를 전부 저장
     */
    @Operation(summary = "lessonId로 모든 유저의 아바타와 애니메이션 다운")
    @GetMapping
    public ResponseEntity<List<AvatarResponseDto>> getAvatar(Long lessonId) {

        log.info("모든 유저의 아바타를 lessonId {} : 로 받습니다.", lessonId);
        List<AvatarResponseDto> avatarList = avatarService.findByLessonId(lessonId);
        log.info("AvatarController avatarList: {}", avatarList);

        return ResponseEntity.ok(avatarList);
    }


    /**
     * img를 AI서버로 전송
     */
    @Operation(
            summary = "img 파일 업로드",
            description = "img 파일을 업로드하면 AI 서버로 전송하여 분석 결과를 zip 형태로 받습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "img 파일 업로드 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "img 파일 example.img 성공적으로 업로드되었습니다."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (파일이 비어있음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "파일이 없습니다."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "파일 처리 중 오류가 발생했습니다."
                            )
                    )
            )
    })
    @PostMapping(value = "/upload-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AvatarResponseDto> sendImgToAiServer(
            @RequestHeader("userId") Long userId
            , @RequestParam("lessonId") Long lessonId
            , @RequestParam("img") MultipartFile img) {

        log.info("userId: {}가, lessonId: {}로, img: {}를 요청했습니다.", userId, lessonId, img);

        try {
            // 1. 파일 존재 여부 검증
            if (img == null || img.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }

            // 2. 파일 크기 검증
            if (img.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }


            String originalFilename = img.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            // 3. 파일 확장자 검증
            if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        // .body("허용되지 않는 파일 형식입니다. jpg, jpeg, png 파일만 업로드 가능합니다.");
                        .body(null);
            }

            // 4. 파일 내용 검증
            try {
                BufferedImage bufferedImage = ImageIO.read(img.getInputStream());
                if (bufferedImage == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            //.body("유효하지 않은 이미지 파일입니다.");
                            .body(null);
                }
            } catch (IOException e) {
                log.error("이미지 파일 검증 중 오류 발생", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }

            // 5. 서비스 호출
            AvatarResponseDto avatarResponseDto = avatarService.sendImgToAiServer(userId, lessonId, img);

            log.info("avatarResponseDto : {}", avatarResponseDto);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    //.body("Img 파일 " + originalFilename + "이 성공적으로 업로드되었습니다.");
                    .body(avatarResponseDto);

        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    //.body("파일 처리 중 오류가 발생했습니다.");
                    .body(null);
        }
    }


    /**
     * 다른 유저의 아바타 이미지, 애니메이션 받기
     */
    @Operation(summary = "다른 유저의 아바타 다운")
    @GetMapping("/participant/{lessonId}/{userId}")
    public ResponseEntity<AvatarResponseDto> getAvatarForParticipant(
            @PathVariable("lessonId") Long lessonId
            , @PathVariable("userId") Long userId) {

        log.info("lessonId : {}의 다른 유저 userId: {}의 아바타를 요청합니다.", lessonId, userId);

        AvatarResponseDto avatarResponseDto = avatarService.findByUserId(lessonId, userId);

        log.info("avatarResponseDto : {}", avatarResponseDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(avatarResponseDto);
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