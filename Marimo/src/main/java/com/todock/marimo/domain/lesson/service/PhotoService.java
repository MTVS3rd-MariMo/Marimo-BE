package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.dto.BackgroundRequestDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
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
import java.util.UUID;

@Slf4j
@Service
public class PhotoService {

    @Value("${external.api.background-server-url}")
    private String AIServerURL;

    @Value("${external.port.server-host}")
    private String serverHost;

    @Value("${external.port.external-port}")
    private String serverPort;

    private final LessonMaterialRepository lessonMaterialRepository;
    private final ParticipantRepository participantRepository;
    private final HotSittingRepository hotSittingRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final String PHOTO_DIR = "data/photo"; // zip 파일 저장 경로
    private static final String BACKGROUND_DIR = "data/background"; // 단체사진 배경 저장 경로

    @Autowired
    public PhotoService(LessonMaterialRepository lessonMaterialRepository
            , ParticipantRepository participantRepository
            , HotSittingRepository hotSittingRepository
            , LessonRepository lessonRepository
            , UserRepository userRepository
            , RestTemplate restTemplate) {
        this.lessonMaterialRepository = lessonMaterialRepository;
        this.participantRepository = participantRepository;
        this.hotSittingRepository = hotSittingRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
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
     * 단체 사진 저장
     */
    @Transactional
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
                        .orElseThrow(() -> new EntityNotFoundException("lessonMaterialId로 수업 자료를 찾을 수 없습니다. : " + lesson.getLessonMaterialId())).getBookContents(),
                lessonId
        );


        // AI 통신
        try {

            HttpHeaders headers = new HttpHeaders(); // 헤더 설정
            headers.setContentType(MediaType.APPLICATION_JSON); // JSON으로 설정

            HttpEntity<BackgroundRequestDto> requestEntity = new HttpEntity<>(backgroundDto, headers);

            // AI 서버 응답
            ResponseEntity<byte[]> AIResponse = restTemplate.exchange(
                    AIServerURL, HttpMethod.POST, requestEntity, byte[].class);

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

    public String savePhotoBackgroundUrl(Long lessonId) {

        return null;
    }
}
