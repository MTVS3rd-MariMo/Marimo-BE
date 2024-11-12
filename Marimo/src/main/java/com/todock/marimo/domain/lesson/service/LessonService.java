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
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.result.repository.ResultRepository;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
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
    private final ResultRepository resultRepository;
    private final RestTemplate restTemplate;
    private final HotSittingRepository hotSittingRepository;

    // 클래스 내부에서 주입된 값을 사용하기 위해 추가
    //@Value("${server.host}")
    private String serverHost = "211.250.74.75";
    // 125.132.216.190:8202
    //@Value("${server.port}")
    private String serverPort = "8202";

    private final ParticipantRepository participantRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String PHOTO_DIR = "data/photo"; // zip 파일 저장 경로
    private static final String BACKGROUND_DIR = "data/background"; // 단체사진 배경 저장 경로

    @Autowired
    public LessonService(LessonMaterialRepository lessonMaterialRepository
            , ParticipantRepository participantRepository
            , ResultRepository resultRepository
            , LessonRepository lessonRepository
            , UserRepository userRepository
            , RestTemplate restTemplate, HotSittingRepository hotSittingRepository) {
        this.lessonMaterialRepository = lessonMaterialRepository;
        this.participantRepository = participantRepository;
        this.resultRepository = resultRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        initDirectories();
        this.hotSittingRepository = hotSittingRepository;
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


    /**
     * 단체 사진 저장
     */
    public void savePhoto(Long lessonId, MultipartFile photo) {

        try {
            String photoName = UUID.randomUUID().toString() + ".png"; // 사진 이름 생성
            Path photoPath = Paths.get(PHOTO_DIR, photoName); // 파일 저장 경로 생성

            if (!photoPath.normalize().startsWith(Paths.get(PHOTO_DIR))) {
                throw new SecurityException("잘못된 파일 경로입니다.");
            }

            Files.write(photoPath, photo.getBytes()); // 파일 저장

            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

            lesson.setPhotoUrl(createPhotoFileUrl(photoName)); // photoUrl 추가

            lessonRepository.save(lesson);

        } catch (IOException e) {
            throw new IllegalArgumentException("사진을 저장하지 못했습니다.");
        }
    }


    /**
     * 사진관에서 사용할 배경 생성 요청
     */
    public void createBackground(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다. lessonId : " + lessonId));

        // AI 서버로 보낼 책 내용 추출
        BackgroundRequestDto backgroundDto = new BackgroundRequestDto(
                lessonMaterialRepository.findById(lesson.getLessonMaterialId())
                        .orElseThrow(() -> new EntityNotFoundException("lessonMaterialId로 수업 자료를 찾을 수 없습니다. : " + lesson.getLessonMaterialId())).getBookContents()
        );

        // AI 통신
        try {

            String AIServerURI = "http://metaai2.iptime.org:7994/marimobackground";

            HttpHeaders headers = new HttpHeaders(); // 헤더 설정
            headers.setContentType(MediaType.APPLICATION_JSON); // JSON으로 설정

            HttpEntity<BackgroundRequestDto> requestEntity = new HttpEntity<>(backgroundDto, headers);

            // AI 서버 응답
            ResponseEntity<byte[]> AIResponse = restTemplate.exchange(
                    AIServerURI, HttpMethod.POST, requestEntity, byte[].class);

            if (AIResponse.getStatusCode() != HttpStatus.OK) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                        , "AI 서버에서 이미지를 가져오는 데 실패함. Status: " + AIResponse.getStatusCode());
            }

            byte[] backgroundFileBytes = AIResponse.getBody();
            String fileName = UUID.randomUUID().toString() + ".png";
            Path zipPath = Paths.get(BACKGROUND_DIR, fileName).normalize();

            if (!zipPath.startsWith(Paths.get(BACKGROUND_DIR))) {
                throw new SecurityException("Invalid file path detected.");
            }

            Files.write(zipPath, backgroundFileBytes);
            lesson.setPhotoBackgroundUrl(createBackgroundFileUrl(fileName));
            lessonRepository.save(lesson);

        } catch (Exception e) {
            log.error("Exception while sending file to AI server", e);
            throw new RuntimeException("Error occurred while processing AI server request", e);
        }
    }


    /**
     * 배경사진 호출
     */
    public String getPhotoBackgroundUrl(Long lessonId) {

        return lessonRepository.findById(lessonId)
                .orElseThrow(() ->
                        new EntityNotFoundException("lessonId로 수업을 조회할 수 없습니다.")).getPhotoBackgroundUrl();
    }


    /**
     * 단체사진 파일 경로를 URL 형식으로 변환
     */
    private String createPhotoFileUrl(String filePath) {
        // 파일 경로에서 중복된 루트 디렉토리를 제거
        String relativePath = filePath.replace("\\", "/");
        return "http://" + serverHost + ":" + serverPort + "/data/photo/" + relativePath;
    }


    /**
     * 배경사진 파일 경로를 URL 형식으로 변환
     */
    private String createBackgroundFileUrl(String filePath) {
        // 파일 경로에서 중복된 루트 디렉토리를 제거
        String relativePath = filePath.replace("\\", "/");
        return "http://" + serverHost + ":" + serverPort + "/data/background/" + relativePath;
    }


}
