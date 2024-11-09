package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.dto.SelfIntroduceRequestDto;
import com.todock.marimo.domain.lesson.dto.WavFileClientToServerRequestDto;
import com.todock.marimo.domain.lesson.service.HotSittingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/api/hot-sitting")
public class HotSittingController {

    private final HotSittingService hotSittingService;

    @Autowired
    public HotSittingController(HotSittingService hotSittingService) {
        this.hotSittingService = hotSittingService;
    }


    /**
     * 자기소개 저장
     */
    @PutMapping("/self-introduce")
    public ResponseEntity<String> hotSittingRecord(@RequestBody SelfIntroduceRequestDto selfIntroduceDto) {

        log.info("Received Request DTO: {}", selfIntroduceDto);

        hotSittingService.saveAIRequest(selfIntroduceDto);

        return ResponseEntity.ok().body("저장 성공했습니다.");
    }


    /**
     * 핫시팅 wavFile AI서버로 전달 - introduceId 추가해서 전달
     */
    @PostMapping(value = "/wav-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> hotSittingWavFile(
            @RequestParam("lessonId") Long lessonId,
            @RequestParam("userName") String userName,
            @RequestParam("character") String character,
            @RequestParam("selfIntNum") Long selfIntNum,
            @RequestPart("wavFile") MultipartFile wavFile) {

        log.info("Received Content-Type: {}", wavFile.getContentType());
        log.info("lessonId: {}", lessonId);
        log.info("userName: {}", userName);
        log.info("character: {}", character);
        log.info("selfIntNum: {}", selfIntNum);
        log.info("wavFile: {}", (wavFile != null ? wavFile.getOriginalFilename() : "null"));

        // MIME 타입과 파일 확장자 확인
        if (wavFile != null) {
            String contentType = wavFile.getContentType();
            String originalFilename = wavFile.getOriginalFilename();

            // MIME 타입 체크
            boolean isWavMimeType = "audio/wav".equals(contentType) || "audio/x-wav".equals(contentType) || "audio/wave".equals(contentType);
            // 파일 확장자 체크
            boolean isWavExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".wav");

            // 로그로 결과 출력
            log.info("Is WAV MIME type: {}", isWavMimeType);
            log.info("Is WAV file extension: {}", isWavExtension);

            // 확인 결과에 따라 추가 작업 수행
            if (!isWavMimeType || !isWavExtension) {
                return ResponseEntity.badRequest().body("파일이 .wav 형식이 아닙니다.");
            }
        } else {
            log.warn("wavFile is null");
            return ResponseEntity.badRequest().body("파일이 전송되지 않았습니다.");
        }

        // WavFileToAIRequestDto 생성 및 설정
        WavFileClientToServerRequestDto wavDto = new WavFileClientToServerRequestDto();
        wavDto.setLessonId(lessonId);
        wavDto.setName(userName);
        wavDto.setCharacter(character);
        wavDto.setSelfIntNum(selfIntNum);

        // 파일을 Base64로 인코딩하여 JSON에 포함
        try {
            byte[] fileBytes = wavFile.getBytes();
            String encodedFile = Base64.getEncoder().encodeToString(fileBytes);
            wavDto.setWavFile(encodedFile);

            log.info("Encoded WAV file content: {}", encodedFile.substring(0, 50) + "...");

        } catch (IOException e) {
            log.error("File encoding error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 인코딩 중 오류 발생");
        }

        log.info("Controller Received DTO: {}", wavDto);

        hotSittingService.sendWavToAiServer(wavDto);

        return ResponseEntity.ok().body("정상적으로 전송되었습니다.");
    }


//    /**
//     * AI가 자기소개에 대한 대답 정리해서 반환
//     */
//    @PutMapping("/record")
//    public
}
