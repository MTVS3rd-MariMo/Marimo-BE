package com.todock.marimo.domain.lesson.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lesson.dto.SelfIntroduceRequestDto;
import com.todock.marimo.domain.lesson.dto.WavFileClientToServerRequestDto;
import com.todock.marimo.domain.lesson.dto.WavFileServerToAIRequestDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lesson.entity.hotsitting.QuestionAnswer;
import com.todock.marimo.domain.lesson.entity.hotsitting.SelfIntroduce;
import com.todock.marimo.domain.lesson.repository.AvatarRepository;
import com.todock.marimo.domain.lesson.repository.HotSittingRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.repository.SelfIntroduceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HotSittingService {

    private final SelfIntroduceRepository selfIntroduceRepository;
    private final HotSittingRepository hotSittingRepository;
    private final LessonRepository lessonRepository;
    private final AvatarRepository avatarRepository;
    private final RestTemplate restTemplate;

    @Value("${external.api.hot-sitting-server-url}")
    private String AIServerURL;


    @Autowired
    public HotSittingService(
            SelfIntroduceRepository selfIntroduceRepository
            , HotSittingRepository hotSittingRepository
            , LessonRepository lessonRepository
            , AvatarRepository avatarRepository
            , RestTemplate restTemplate) {
        this.selfIntroduceRepository = selfIntroduceRepository;
        this.hotSittingRepository = hotSittingRepository;
        this.lessonRepository = lessonRepository;
        this.avatarRepository = avatarRepository;
        this.restTemplate = restTemplate;
    }


    /**
     * 핫시팅 질의응답 wavFile을 AI 서버로 전송하고 SelfIntroduceId를 포함합니다.
     */
    @Transactional
    public void sendWavToAiServer(Long userId, WavFileClientToServerRequestDto wavDto) {

        log.info("질의응답의 userId : {}, wavDto : {}", userId, wavDto);

        // userId와 character 연결하기, 수업을 lessonId로 찾기
        Lesson lesson = lessonRepository.findById(wavDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

        // 전달받은 lessonId와 selfIntNum 로그 출력
        Long lessonId = wavDto.getLessonId();
        Long selfIntNum = wavDto.getSelfIntNum();
        log.info("wavDto - selfIntNum: {}, lessonId: {}", selfIntNum, lessonId);

        // 아바타에 캐릭터명 저장
        Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(lessonId, userId)
                .orElseThrow(() -> new EntityNotFoundException("lessonId와 userId로 아바타를 찾을 수 없습니다."));
        // 아바타에 캐릭터 명이 존재하지 않을 경우 전달받은 캐릭터 이름으로 업데이트
        if (avatar.getCharacter() != null) {
            avatar.setCharacter(wavDto.getCharacter());
            avatarRepository.save(avatar); // 아바타에 캐릭터명 저장
        }

        // lesson에서 HotSitting 찾기
        HotSitting hotSitting = lesson.getHotSitting();
        if (hotSitting == null) {
            throw new EntityNotFoundException("HotSitting 엔티티를 찾을 수 없습니다.");
        }

        log.info("hotSittingId : {}, selfIntNum : {}", hotSitting.getHotSittingId(), selfIntNum);

        // SelfIntroduce 엔티티를 lessonId로
        SelfIntroduce selfIntroduce = selfIntroduceRepository
                .findByHotSitting_hotSittingIdAndSelfIntNum(hotSitting.getHotSittingId(), selfIntNum);
        if (selfIntroduce == null) {
            throw new EntityNotFoundException("HotSittingId와 selfIntNum으로 자기소개를 찾을 수 없습니다.");
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

        // 필수 필드 null 체크
        if (wavDto.getLessonId() == null || wavDto.getSelfIntroductionId() == null ||
                wavDto.getName() == null || wavDto.getCharacter() == null ||
                wavDto.getWavFile() == null) {
            throw new RuntimeException("===========================\n필수 필드가 비어 있습니다.\n============================\n");
        }

        // AI 서버로 전송
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청에 사용될 HttpEntity 생성
            HttpEntity<WavFileServerToAIRequestDto> requestEntity = new HttpEntity<>(wavServerToAIDto, headers);

            // AI 서버로 POST 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(AIServerURL, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("AI 서버로 파일 전송 성공: {}", response.getBody());
                saveAIResponse(response.getBody());
            } else {
                throw new RuntimeException("AI 서버로 파일 전송 실패 - 상태 코드: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("AI 서버로 파일 전송 중 예외 발생", e);
            throw new RuntimeException("AI 서버로 파일 전송 중 오류 발생", e);
        }

    }


    /**
     * 핫시팅 자기소개 저장
     */
    public void saveSelfIntroduce(Long userId, SelfIntroduceRequestDto selfIntroduceDto) {

        // 수업 찾기
        Lesson lesson = lessonRepository.findById(selfIntroduceDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

        log.info("userId: {}", userId);
        log.info(selfIntroduceDto.toString());
        // 핫시팅 찾기
        HotSitting hotSitting = lesson.getHotSitting();

        // 핫시팅에 자기소개 추가
        SelfIntroduce selfIntroduce = new SelfIntroduce(
                hotSitting,
                userId,
                selfIntroduceDto.getSelfIntNum(),
                selfIntroduceDto.getSelfIntroduce()
        );

        // 핫시팅에 자기소개 추가
        hotSitting.getSelfIntroduces().add(selfIntroduce);

        // 변경된 hotSitting 엔티티를 다시 저장
        selfIntroduceRepository.save(selfIntroduce);

        // 변경된 핫시팅을 다시 저장하여 관계 갱신
        hotSittingRepository.save(hotSitting);
    }


    /**
     * AI 서버 응답을 처리하고 저장하는 메서드
     */
    private void saveAIResponse(String responseBody) {
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

}
