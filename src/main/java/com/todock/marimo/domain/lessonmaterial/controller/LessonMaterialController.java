package com.todock.marimo.domain.lessonmaterial.controller;

import com.todock.marimo.domain.lessonmaterial.dto.DetailLessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialNameResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.LessonMaterialNamesRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.LessonMaterialRequestDto;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "LessonMaterial API", description = "수업 자료 관련 API")
public class LessonMaterialController {

    private final LessonMaterialService lessonMaterialService;

    @Autowired
    public LessonMaterialController(LessonMaterialService lessonMaterialService) {
        this.lessonMaterialService = lessonMaterialService;
    }


    /**
     * pdf 업로드 하고 수업자료 Id, 퀴즈 8개, 열린 질문 2개를 클라이언트로 반환
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
    public ResponseEntity<LessonMaterialResponseDto> teacherSendPdfToAiServer(
            @RequestHeader("userId") Long userId,
            @RequestPart("pdf") MultipartFile pdfFile,
            @RequestParam("bookTitle") String bookTitle,
            @RequestParam("author") String author) {

        if (pdfFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        LessonMaterialResponseDto lessonMaterialResponseDto
                = lessonMaterialService.sendPdfToAiServer(pdfFile, userId, bookTitle, author);

        return ResponseEntity.status(HttpStatus.OK).body(lessonMaterialResponseDto);
    }


    /**
     * 수업자료 저장 - 수업 자료 id, 선택한 퀴즈 2개, 열린 질문 2개
     */
    @Operation(
            summary = "수업 자료 수정 후 생성",
            description = "수업 자료 수정 후 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LessonMaterialRequestDto.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "수업 자료 생성 성공",
                    content = @Content(schema = @Schema(implementation = LessonMaterialRequestDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PutMapping
    public ResponseEntity<String> teacherUpdateLessonMaterial(
            @RequestHeader("userId") Long userId,
            @Valid @RequestBody LessonMaterialRequestDto lessonMaterialRequestDto) {

        try {
            lessonMaterialService.updateLessonMaterial(lessonMaterialRequestDto);

            return ResponseEntity.status(HttpStatus.CREATED).body("수업 자료를 생성했습니다.");
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수업 자료 생성 중 오류가 발생했습니다.");
        }
    }


    /**
     * lessonMaterialId로 수업자료 상세 조회 - 수정
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
    @GetMapping("detail/{lessonMaterialId}")
    public ResponseEntity<DetailLessonMaterialDto> teacherGetLessonMaterialByLessonMaterialId(
            @RequestHeader("userId") Long userId,
            @PathVariable("lessonMaterialId") Long lessonMaterialId) {

        log.info("상세 조회할 수업 자료의 lessonMaterialId: {}", lessonMaterialId);

        DetailLessonMaterialDto updateLessonMaterialDto = lessonMaterialService.findById(lessonMaterialId);

        return ResponseEntity.ok(updateLessonMaterialDto);
    }


    /**
     * userId로 lessonMaterial 전체 조회
     */
    @Operation(summary = "userId로 수업 자료 전체 조회")
    @GetMapping
    public ResponseEntity<LessonMaterialNamesRequestDto> teacherGetLessonMaterialNames(
            @RequestHeader("userId") Long userId) {

        List<LessonMaterialNameResponseDto> lessonMaterialNameResponseDtos
                = lessonMaterialService.getLessonMaterialByUserId(userId);
        LessonMaterialNamesRequestDto responseDto = new LessonMaterialNamesRequestDto(lessonMaterialNameResponseDtos);

        return ResponseEntity.ok(responseDto);
    }


    /**
     * 수업자료 id로 수업자료 삭제
     */
    @Operation(summary = "lessonMaterialId로 수업 자료 삭제", description = "수업 자료를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @DeleteMapping("/{lessonMaterialId}")
    public ResponseEntity<String> teacherDeleteLessonMaterial(
            @RequestHeader("userId") Long userId,
            @PathVariable("lessonMaterialId") Long lessonMaterialId) {

        lessonMaterialService.deleteById(lessonMaterialId);

        return ResponseEntity.ok("lessonMaterialId : " + lessonMaterialId + "인 수업자료를 삭제했습니다.");

    }

}