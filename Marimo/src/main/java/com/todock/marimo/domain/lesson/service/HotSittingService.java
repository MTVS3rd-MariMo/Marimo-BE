package com.todock.marimo.domain.lesson.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lesson.dto.WavFileToAIRequestDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lesson.repository.HotSittingRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HotSittingService {

    private final HotSittingRepository hotSittingRepository;
    private final LessonRepository lessonRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public HotSittingService(
            HotSittingRepository hotSittingRepository
            , LessonRepository lessonRepository
            , ObjectMapper objectMapper
            , RestTemplate restTemplate
    ) {
        this.hotSittingRepository = hotSittingRepository;
        this.lessonRepository = lessonRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }


    /**
     * 핫시팅 wavFile AI서버로 전달 - introduceId 추가해서 전달
     */
    public void sendWavToAiServer(WavFileToAIRequestDto wavDto) {

        String AIServerUrI = "http://metaai2.iptime.org:61987/hotseating";

        Long selfIntNum = wavDto.getSelfIntNum();
        Long lessonId = wavDto.getLessonId();

        // 파라미터 값 확인을 위한 로그 추가
        log.info("Received selfIntNum: {}, lessonId: {}", lessonId, selfIntNum);

        // userName, lessonId로 hotSitting 찾기
        HotSitting hotSitting = hotSittingRepository.findByLesson_lessonIdAndSelfIntNum(lessonId, selfIntNum);

        if (hotSitting == null) {
            log.error("HotSitting not found for lessonId: {}, selfIntNum: {}", lessonId, selfIntNum);
        }

        // hotSitting 에서 hotSittingId 추출 후 적용
        wavDto.setSelfIntroductionId(hotSitting.getHotSittingId());
        log.info("Service Received DTO: {}", wavDto);

        // 폼 데이터 구성
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("selfIntroductionId", wavDto.getSelfIntroductionId());
        bodyMap.add("lessonId", wavDto.getLessonId());
        bodyMap.add("name", wavDto.getName());
        bodyMap.add("character", wavDto.getCharacter());
        bodyMap.add("selfIntNum", wavDto.getSelfIntNum());
        bodyMap.add("wavFile", wavDto.getWavFile().getResource()); // MultipartFile을 Resource로 변환하여 추가

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

        // 요청 보내기
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(AIServerUrI, requestEntity, String.class);

            // 상태 코드가 200인 경우 성공 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("AI 서버로 파일 전송 성공: {}", response.getBody());
            } else {
                // 200이 아닌 경우 예외 발생
                throw new RuntimeException("AI 서버로 파일 전송 실패 - 상태 코드: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("AI 서버로 파일 전송 중 예외 발생", e);
            // 필요시 추가 예외 처리
        }

    }
}
