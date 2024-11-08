package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.dto.WavFileToAIRequestDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lesson.repository.HotSittingRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lessonresult.dto.SelfIntroduceRequestDto;
import com.todock.marimo.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HotSittingService {

    private final HotSittingRepository hotSittingRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public HotSittingService(
            HotSittingRepository hotSittingRepository
            , LessonRepository lessonRepository
            , UserRepository userRepository
            , RestTemplate restTemplate) {

        this.hotSittingRepository = hotSittingRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * 핫시팅 자기소개 저장
     */
    public void registIntroduce(Long userId, SelfIntroduceRequestDto selfIntroduceRequestDto) {

        Lesson lesson = lessonRepository.findById(selfIntroduceRequestDto.getLessonId()) // lesson 생성
                .orElseThrow(() -> new IllegalArgumentException("LessonId가 없습니다."));

        HotSitting hotSitting = new HotSitting(
                lesson,
                userId,
                selfIntroduceRequestDto.getSelfIntNum(),
                selfIntroduceRequestDto.getSelfIntroduce()
        );
        lesson.getHotSittings().add(hotSitting); // 핫시팅 자기소개 등록
        log.info("userId: {}, selfIntroduce: {} ", hotSitting.getUserId(), hotSitting.getSelfIntroduce());
        hotSittingRepository.save(hotSitting);
    }


    /**
     * 핫시팅 wavFile AI서버로 전달 - introduceId 추가해서 전달
     */
    public void sendWavToAiServer(WavFileToAIRequestDto wavDto) {

        String AIServerUrI = "AI SERVER URI";

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
        log.info("sendWavToAiServer : {}", wavDto.getSelfIntroductionId());

        // 핫시팅 ID 추가 설정
        wavDto.setSelfIntroductionId(hotSitting.getHotSittingId());

        // MultiValueMap을 통해 데이터 구성
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("selfIntroductionId", wavDto.getSelfIntroductionId());
        bodyMap.add("lessonId", wavDto.getLessonId());
        bodyMap.add("userName", wavDto.getUserName());
        bodyMap.add("character", wavDto.getCharacter());
        bodyMap.add("wavFile", wavDto.getWavFile().getResource()); // 파일은 Resource로 추가
        bodyMap.add("selfIntNum", wavDto.getSelfIntNum());

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

        // 요청 보내기
        ResponseEntity<String> response = restTemplate.postForEntity(AIServerUrI, requestEntity, String.class);

        // 응답 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("AI 서버로 파일 전송 성공: " + response.getBody());
        } else {
            System.out.println("AI 서버로 파일 전송 실패: " + response.getStatusCode());
        }
    }

}
