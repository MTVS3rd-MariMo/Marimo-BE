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

        Lesson lesson = lessonRepository.findById(wavDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

        Long lessonId = wavDto.getLessonId();
        Long selfIntNum = wavDto.getSelfIntNum();

        Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(lessonId, userId)
                .orElseThrow(() -> new EntityNotFoundException("lessonId와 userId로 아바타를 찾을 수 없습니다."));

        if (avatar.getCharacter() == null) {
            avatar.setCharacter(wavDto.getCharacter());
            log.info(avatar.getCharacter() + "로 저장되었습니다.");
            avatarRepository.save(avatar);
        } else {
            log.info("이미 캐릭터가 있습니다.");
        }

        HotSitting hotSitting = lesson.getHotSitting();

        if (hotSitting == null) {
            throw new EntityNotFoundException("HotSitting 엔티티를 찾을 수 없습니다.");
        }

        log.info("hotSittingId : {}, selfIntNum : {}", hotSitting.getHotSittingId(), selfIntNum);

        SelfIntroduce selfIntroduce = selfIntroduceRepository
                .findByHotSitting_hotSittingIdAndSelfIntNum(hotSitting.getHotSittingId(), selfIntNum);
        if (selfIntroduce == null) {
            throw new EntityNotFoundException("HotSittingId와 selfIntNum으로 자기소개를 찾을 수 없습니다.");
        }

        wavDto.setSelfIntroductionId(selfIntroduce.getSelfIntroduceId());

        WavFileServerToAIRequestDto wavServerToAIDto = new WavFileServerToAIRequestDto(
                wavDto.getLessonId(),
                wavDto.getSelfIntroductionId(),
                wavDto.getName(),
                wavDto.getCharacter(),
                wavDto.getWavFile()
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WavFileServerToAIRequestDto> requestEntity = new HttpEntity<>(wavServerToAIDto, headers);

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

        Lesson lesson = lessonRepository.findById(selfIntroduceDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));
        HotSitting hotSitting = lesson.getHotSitting();

        SelfIntroduce selfIntroduce = new SelfIntroduce(
                hotSitting,
                userId,
                selfIntroduceDto.getSelfIntNum(),
                selfIntroduceDto.getSelfIntroduce()
        );
        hotSitting.getSelfIntroduces().add(selfIntroduce);

        selfIntroduceRepository.save(selfIntroduce);
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

            saveAIResponse(selfIntroduceId, contents);

        } catch (Exception e) {
            log.error("AI 서버와 통신 중 에러가 발생했습니다.", e);
        }
    }


    /**
     * AI 서버 응답을 데이터베이스에 저장하는 메서드
     */
    private void saveAIResponse(Long selfIntroduceId, String qnAContents) {

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
