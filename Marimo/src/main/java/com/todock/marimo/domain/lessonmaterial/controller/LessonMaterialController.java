package com.todock.marimo.domain.lessonmaterial.controller;

    import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialRegistRequestDto;
    import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
    import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

    import java.util.List;

@RequestMapping("api/v1/lesson-material")
@RestController
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
     *      HttpHeaders: 응답 헤더 정보
     *      HttpBody: 실제 응답 데이터
     */


    // swagger
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
    @PostMapping
    public ResponseEntity<LessonMaterialRegistRequestDto> createLessonMaterial(
            @RequestBody LessonMaterialRegistRequestDto lessonMaterialRegistRequestDto) {
        
        // 1. 서비스에 복합 DTO 전달하여 저장 로직 처리
        LessonMaterial savedLessonMaterial = lessonMaterialService.save(userId, lessonMaterialRegistRequestDto);
    }

    /**
     * 수업자료 id로 수업자료 수정
     */

    @PutMapping
    public LessonMaterial update(LessonMaterial lessonMaterial) {

    }

    /**
     * 수업자료 id로 수업자료 삭제
     */


    /**
     * userId로 수업자료 전체 불러오기
     */
    @GetMapping
    public List<LessonMaterial> getAll() {

    }

    /**
     * 수업자료 id로 불러오기
     */
    @GetMapping
    public List<LessonMaterial> getByLessonMaterialId(Long id) {

    }

}
