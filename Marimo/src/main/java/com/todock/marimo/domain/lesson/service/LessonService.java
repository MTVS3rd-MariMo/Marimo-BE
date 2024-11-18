package com.todock.marimo.domain.lesson.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lesson.dto.BackgroundRequestDto;
import com.todock.marimo.domain.lesson.dto.ParticipantListDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.Participant;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lesson.repository.HotSittingRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.repository.ParticipantRepository;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * 수업 서비스 클래스: 수업 생성, 참가자 관리, 열린 질문 저장 등의 기능을 담당
 */
@Slf4j
@Service
public class LessonService {

    private final LessonMaterialRepository lessonMaterialRepository;
    private final ParticipantRepository participantRepository;
    private final HotSittingRepository hotSittingRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    private static final String PHOTO_DIR = "data/photo"; // zip 파일 저장 경로
    private static final String BACKGROUND_DIR = "data/background"; // 단체사진 배경 저장 경로

    @Autowired
    public LessonService(LessonMaterialRepository lessonMaterialRepository
            , ParticipantRepository participantRepository
            , HotSittingRepository hotSittingRepository
            , LessonRepository lessonRepository
            , UserRepository userRepository) {
        this.lessonMaterialRepository = lessonMaterialRepository;
        this.participantRepository = participantRepository;
        this.hotSittingRepository = hotSittingRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        initDirectories();
    }

    // 필요한 디렉토리를 초기화 하는 메서드
    public void initDirectories() {
        try { // 디렉토리 생성
            Files.createDirectories(Paths.get(PHOTO_DIR)); // PHOTO 파일 저장 경로
            Files.createDirectories(Paths.get(BACKGROUND_DIR)); // PHOTO 파일 저장 경로

        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
    }


    /**
     * 수업 생성 - lessonMaterialId를 받고 수업 자료와 LessonId 반환
     */
    @Transactional
    public Long createLesson(Long userId, Long lessonMaterialId) {

        // Lesson 생성 및 설정
        Lesson newLesson = new Lesson(userId, lessonMaterialId);

        // Lesson을 먼저 저장하여 ID를 생성합니다.
        lessonRepository.save(newLesson);

        // HotSitting 생성 후 Lesson과 연결
        HotSitting newHotSitting = new HotSitting();
        newHotSitting.setLesson(newLesson);

        // HotSitting 저장
        hotSittingRepository.save(newHotSitting);

        // Lesson에 HotSitting 설정 후 다시 저장
        newLesson.setHotSitting(newHotSitting);
        lessonRepository.save(newLesson);

        Long lessonId = newLesson.getLessonId(); // lessonId 추출

        log.info("\n\n생성된 lessonId : {}\n\n", lessonId);
        log.info("\n\n적용된 lessonMaterialId : {}\n\n", newLesson.getLessonMaterialId());

        return lessonId;

    }


    /**
     * LessonId로 참가자 목록에 유저Id, 유저 이름 추가하기
     */
    @Transactional
    public void updateUserByLessonId(Long userId, Long lessonId) {

        // 수업 찾기
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다."));

        // 참가자 생성 및 정보 설정
        Participant participant = new Participant();
        participant.setUserId(userId);

        // 유저 이름 설정
        String participantName = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId))
                .getName();
        participant.setParticipantName(participantName);
        participant.setLesson(lesson);

        // 참가자 저장
        participantRepository.save(participant);

    }


    /**
     * LessonId로 참가자 목록을 조회하여 반환
     */
    public ParticipantListDto findParticipantByLessonId(Long lessonId) {

        // lessonId에 해당하는 수업 조회
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("LessonId에 맞는 수업이 없습니다"));

        // lessonId와 연결된 Participant 목록에서 userId만 추출하여 List<Long>으로 변환
        List<Long> participantUserIds = lesson.getParticipantList().stream()
                .map(Participant::getUserId) // Participant의 userId만 추출
                .toList();

        // ParticipantListDto 생성 및 반환
        return new ParticipantListDto(participantUserIds);
    }

}
