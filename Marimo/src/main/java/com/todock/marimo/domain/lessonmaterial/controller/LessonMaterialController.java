package com.todock.marimo.domain.lessonmaterial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lesson.service.LessonService;
import com.todock.marimo.domain.lessonmaterial.dto.QuizDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialNameResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.LessonMaterialNamesRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.LessonMaterialRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.OpenQuestionRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.QuizRequestDto;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<LessonMaterialResponseDto> sendPdfToAiServer(
            @RequestHeader("userId") Long userId,
            @RequestParam("pdf") MultipartFile pdfFile) {

        log.info("수업 자료 생성 요청 보내는 유저의 userId : {}와 pdf : {}", userId, pdfFile);

        if (pdfFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } // 파일 용량 null 확인

        LessonMaterialResponseDto lessonMaterialResponseDto
                = lessonMaterialService.sendPdfToAiServer(pdfFile, userId);

        log.info("수정한 값: {}", lessonMaterialResponseDto);

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
    public ResponseEntity<String> updateLessonMaterial(
            @RequestBody LessonMaterialRequestDto lessonMaterialRequestDto) {

        log.info("수업 자료 생성 후 수정한 열린 질문, 퀴즈 2개가 있는 DTO : {} ", lessonMaterialRequestDto); // JSON 데이터 로깅

        try {
            log.info("수업 자료 ID: {}", lessonMaterialRequestDto.getLessonMaterialId());

            // 열린 질문 리스트 확인 및 출력
            List<OpenQuestionRequestDto> openQuestions = lessonMaterialRequestDto.getOpenQuestionList();
            log.info("openQuestions: {}", openQuestions.toString());


            // 퀴즈 리스트 확인 및 출력
            List<QuizDto> quizList = lessonMaterialRequestDto.getQuizList();
            log.info("quizList: {}", quizList.toString());

            // 서비스 호출
            lessonMaterialService.updateLessonMaterial(lessonMaterialRequestDto);
            log.info("저장 완료");

            return ResponseEntity.status(HttpStatus.CREATED).body("수업 자료를 생성했습니다.");
        } catch (Exception e) {
            log.error("DTO 매핑 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수업 자료 생성 중 오류가 발생했습니다.");
        }
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
    public ResponseEntity<Optional<LessonMaterial>> getLessonMaterialByLessonMaterialId(
            @PathVariable("lessonMaterialId") Long lessonMaterialId) {

        log.info("상세 조회할 수업 자료의 lessonMaterialId: {}", lessonMaterialId);

        return ResponseEntity.ok(lessonMaterialService.findById(lessonMaterialId));
    }


    /**
     * userId로 lessonMaterial 전체 조회
     */
    @Operation(summary = "userId로 수업 자료 전체 조회")
    @GetMapping
    public ResponseEntity<LessonMaterialNamesRequestDto> getLessonMaterialNames(
            @RequestHeader("userId") Long userId) {

        log.info("유저의 userId : {}로 가지고 있는 수업 자료 리스트 조회", userId);
        
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
    public ResponseEntity<String> deleteLessonMaterial(@PathVariable("lessonMaterialId") Long lessonMaterialId) {

        lessonMaterialService.deleteById(lessonMaterialId);

        return ResponseEntity.ok("수업자료를 삭제했습니다.");

    }

}