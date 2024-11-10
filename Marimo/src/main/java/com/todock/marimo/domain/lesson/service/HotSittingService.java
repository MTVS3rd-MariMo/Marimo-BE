package com.todock.marimo.domain.lesson.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lesson.dto.SelfIntroduceRequestDto;
import com.todock.marimo.domain.lesson.dto.WavFileClientToServerRequestDto;
import com.todock.marimo.domain.lesson.dto.WavFileServerToAIRequestDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lesson.entity.hotsitting.QuestionAnswer;
import com.todock.marimo.domain.lesson.entity.hotsitting.SelfIntroduce;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.repository.SelfIntroduceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HotSittingService {

    private final SelfIntroduceRepository selfIntroduceRepository;
    private final RestTemplate restTemplate;
    private final LessonRepository lessonRepository;

    @Autowired
    public HotSittingService(
            SelfIntroduceRepository selfIntroduceRepository
            , RestTemplate restTemplate, LessonRepository lessonRepository) {
        this.selfIntroduceRepository = selfIntroduceRepository;
        this.restTemplate = restTemplate;
        this.lessonRepository = lessonRepository;
    }

    /**
     * 핫시팅 wavFile을 AI 서버로 전송하고 SelfIntroduceId를 포함합니다.
     */
    public void sendWavToAiServer(WavFileClientToServerRequestDto wavDto) {

        // 전달받은 lessonId와 selfIntNum 로 로그 출력
        Long lessonId = wavDto.getLessonId();
        Long selfIntNum = wavDto.getSelfIntNum();
        log.info("Received selfIntNum: {}, lessonId: {}", selfIntNum, lessonId);

        // 수업을 lessonId로 찾기
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

        // lesson에서 HotSitting 찾기
        HotSitting hotSitting = lesson.getHotSitting();
        if (hotSitting == null) {
            throw new EntityNotFoundException("HotSitting 엔티티를 찾을 수 없습니다.");
        }

        // SelfIntroduce 엔티티를 hotSittingId와 selfIntNum으로 찾기
        SelfIntroduce selfIntroduce = selfIntroduceRepository
                .findByHotSitting_HotSittingIdAndSelfIntNum(hotSitting.getHotSittingId(), selfIntNum);
        if (selfIntroduce == null) {
            throw new EntityNotFoundException("selfIntroduce를 찾을 수 없습니다.");
        }

        // wavDto에 selfIntroduceId 추가
        wavDto.setSelfIntroductionId(selfIntroduce.getSelfIntroduceId());

        // AI 전달용 Dto로 옮기기
        WavFileServerToAIRequestDto wavServerToAIDto = new WavFileServerToAIRequestDto();
        wavServerToAIDto.setLessonId(wavDto.getLessonId());
        wavServerToAIDto.setSelfIntroduceId(wavDto.getSelfIntroductionId());
        wavServerToAIDto.setName(wavDto.getName());
        wavServerToAIDto.setCharacter(wavDto.getCharacter());
        wavServerToAIDto.setWavFile(wavDto.getWavFile());

        // 필드 설정 후 wavDto에 저장된 데이터 확인
        // log.info("AI로 보내기 전 최종 DTO 확인 : " + wavServerToAIDto.toString());

        // 필수 필드 null 체크
        if (wavDto.getLessonId() == null || wavDto.getSelfIntroductionId() == null ||
                wavDto.getName() == null || wavDto.getCharacter() == null ||
                wavDto.getWavFile() == null) {
            throw new RuntimeException("===========================\n필수 필드가 비어 있습니다.\n============================\n");
        }

        // AI 서버로 전송
        try {

            String AIServerUrI = "http://metaai2.iptime.org:64987/hotseating";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청에 사용될 HttpEntity 생성
            HttpEntity<WavFileServerToAIRequestDto> requestEntity = new HttpEntity<>(wavServerToAIDto, headers);

            // AI 서버로 POST 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(AIServerUrI, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("AI 서버로 파일 전송 성공: {}", response.getBody());
                handleAIResponse(response.getBody());
            } else {
                throw new RuntimeException("AI 서버로 파일 전송 실패 - 상태 코드: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("AI 서버로 파일 전송 중 예외 발생", e);
            throw new RuntimeException("AI 서버로 파일 전송 중 오류 발생", e);
        }
    }


    /**
     * AI 서버 응답을 처리하고 저장하는 메서드
     */
    private void handleAIResponse(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 응답 파싱
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            Long lessonId = jsonNode.get("lessonId").asLong();
            Long selfIntroduceId = jsonNode.get("selfIntroduceId").asLong();
            String contents = jsonNode.get("contents").asText();

            // 로그 출력
            log.info("Received AI Response - lessonId: {}, selfIntroduceId: {}, contents: {}", lessonId, selfIntroduceId, contents);

            // 저장 로직 호출
            saveAIResponse(lessonId, selfIntroduceId, contents);

        } catch (Exception e) {
            log.error("Failed to parse AI server response", e);
        }
    }


    /**
     * AI 서버 응답을 데이터베이스에 저장하는 메서드
     */
    private void saveAIResponse(Long lessonId, Long selfIntroduceId, String qnAContents) {

        SelfIntroduce selfIntroduce = selfIntroduceRepository.findById(selfIntroduceId)
                .orElseThrow(() -> new EntityNotFoundException("selfIntroduceId로 자기소개를 찾을 수 없습니다."));

        selfIntroduce.getQuestionAnswers().add(
                new QuestionAnswer(
                        selfIntroduce,
                        qnAContents
                )
        );
        selfIntroduceRepository.save(selfIntroduce);

    }


    /**
     * 핫시팅 자기소개 저장
     */
    public void saveAIRequest(SelfIntroduceRequestDto selfIntroduceDto) {

        // 수업 찾기
        Lesson lesson = lessonRepository.findById(selfIntroduceDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

        // 핫시팅 찾기
        HotSitting hotSitting = lesson.getHotSitting();

        // 핫시팅에 자기소개 추가
        SelfIntroduce selfIntroduce = new SelfIntroduce(
                hotSitting,
                selfIntroduceDto.getSelfIntNum(),
                selfIntroduceDto.getSelfIntroduce()
        );

        // 핫시팅에 자기소개 추가
        hotSitting.getSelfIntroduces().add(selfIntroduce);

        // 변경된 hotSitting 엔티티를 다시 저장
        selfIntroduceRepository.save(selfIntroduce);

        // 변경된 핫시팅을 다시 저장하여 관계 갱신
        // hotSittingRepository.save(hotSitting);
    }


}
