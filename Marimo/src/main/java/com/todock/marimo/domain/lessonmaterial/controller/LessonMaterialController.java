package com.todock.marimo.domain.lessonmaterial.controller;

import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialNameDto;
import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialNameResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialRegistRequestDto;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lesson-material")
public class LessonMaterialController {

    private final LessonMaterialService lessonMaterialService;

    @Autowired
    public LessonMaterialController(LessonMaterialService lessonMaterialService) {
        this.lessonMaterialService = lessonMaterialService;
    }

    /**
     * pdf 전달
     * 1. yml 파일 설정
     * 2. POST 엔드포인트 생성 - 파일 업로드 메서드 작성 - pdf 파일 수신 확인
     * 2-2. HttpStatus: HTTP 상태 코드 (200 OK, 404 Not Found 등)
     * HttpHeaders: 응답 헤더 정보
     * HttpBody: 실제 응답 데이터
     */


    /**
     * pdf 업로드
     */
    @Operation(
            summary = "PDF 파일 업로드",
            description = "PDF 파일을 업로드하면 AI 서버로 전송하여 분석 결과를 JSON 형태로 받습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF 파일 업로드 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "PDF 파일 example.pdf이 성공적으로 업로드되었습니다."
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
    @PostMapping("/upload-pdf")
    public ResponseEntity<String> sendPdfToAiServer(@RequestParam("pdf") MultipartFile pdfFile) {

        if (pdfFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 없습니다.");
        }

        String fileName = pdfFile.getOriginalFilename(); // 파일 이름

        lessonMaterialService.sendPdfToAiServer(pdfFile);

        return ResponseEntity.status(HttpStatus.OK).body("PDF 파일" + fileName + "이 성공적으로 업로드되었습니다.");
    }


    /**
     * 수업자료 저장
     */
    @Operation(summary = "수업 자료 생성", description = "새로운 수업 자료를 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "수업 자료 생성 성공",
                    content = @Content(schema = @Schema(implementation = LessonMaterialRegistRequestDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping
    public ResponseEntity<LessonMaterialRegistRequestDto> createLessonMaterial(
            @RequestBody LessonMaterialRegistRequestDto lessonMaterialRegistRequestDto) {

        // 1. 서비스에 복합 DTO 전달하여 저장 로직 처리
        LessonMaterial savedLessonMaterial = lessonMaterialService.save(lessonMaterialRegistRequestDto);
        log.info("저장 완료");
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonMaterialRegistRequestDto);
    }


    /**
     * 유저 id로 유저의 수업 자료 전체 조회 (pdf 이름만 보여줌)
     */
    @Operation(summary = "유저의 수업 자료 조회", description = "유저 ID로 해당 유저의 모든 수업 자료를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = LessonMaterialNameDto.class))
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<LessonMaterialNameResponseDto>> getLessonMaterialByUserId(
            @Parameter(description = "수업 자료 id", required = true, example = "1")
            @PathVariable("userId") Long userId) {

        List<LessonMaterialNameResponseDto> LessonMaterialNameList = lessonMaterialService.getLessonMaterialByUserId(userId);

        return ResponseEntity.ok(LessonMaterialNameList);
    }


    /**
     * lessonMaterialId로 수업 자료 내용 상세 조회
     */
    @Operation(summary = "수업 자료 상세 조회", description = "수업 자료 ID로 상세 내용을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = LessonMaterial.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수업 자료를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/{lessonMaterialId}/detail")
    public ResponseEntity<LessonMaterial> getLessonMaterialByLessonMaterialId(
            @PathVariable("lessonMaterialId") Long lessonMaterialId) {

        LessonMaterial lessonMaterial = lessonMaterialService.getLessonMaterialByLessonMaterialId(lessonMaterialId);

        return ResponseEntity.ok(lessonMaterial);
    }

    /**
     * 수업자료 id로 수업자료 수정
     */
    @Operation(summary = "수업 자료 수정", description = "수업 자료를 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PutMapping("/{lessonMaterialId}")
    public ResponseEntity<String> updateLessonMaterial(
            @PathVariable("lessonMaterialId") Long lessonMaterial,
            @RequestBody LessonMaterialRegistRequestDto updateLessonMaterialInfo) {

        lessonMaterialService.updateLessonMaterial(lessonMaterial, updateLessonMaterialInfo);

        return ResponseEntity.ok("수정 완료");
    }


    /**
     * 수업자료 id로 수업자료 삭제
     */
    @Operation(summary = "수업 자료 삭제", description = "수업 자료를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @DeleteMapping("/{lessonMaterialId}")
    public ResponseEntity<String> deleteLessonMaterial(@PathVariable("lessonMaterialId") Long lessonMaterialId) {

        lessonMaterialService.deleteById(lessonMaterialId);

        return ResponseEntity.ok("수업자료를 삭제했습니다.");

    }

}