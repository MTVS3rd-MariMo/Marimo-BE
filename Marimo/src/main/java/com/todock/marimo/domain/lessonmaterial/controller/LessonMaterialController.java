package com.todock.marimo.domain.lessonmaterial.controller;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("api/v1/lesson-material")
@RestController
public class LessonMaterialController {

    @Autowired
    private final LessonMaterialService lessonMaterialService;

    public LessonMaterialController(LessonMaterialService lessonMaterialService) {
        this.lessonMaterialService = lessonMaterialService;
    }

    /**
     * pdf 전달
     * <p>
     * 1. yml 파일 설정
     * 2. POST 엔드포인트 생성 - 파일 업로드 메서드 작성 - pdf 파일 수신 확인
     * 2-2. HttpStatus: HTTP 상태 코드 (200 OK, 404 Not Found 등)
     *      HttpHeaders: 응답 헤더 정보
     *      HttpBody: 실제 응답 데이터
     * 3. 
     */
    @PostMapping("/upload-pdf")
    public ResponseEntity<String> sendPdfToAiServer(@RequestParam("file") MultipartFile pdfFile) {

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
    public LessonMaterial save(LessonMaterial lessonMaterial) {

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
