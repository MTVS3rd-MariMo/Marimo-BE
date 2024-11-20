package com.todock.marimo.domain.lesson.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.todock.marimo.domain.lesson.dto.BackgroundRequestDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
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

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final LessonMaterialRepository lessonMaterialRepository;
    private final LessonRepository lessonRepository;
    private final RestTemplate restTemplate;

    private static final String PHOTO_DIR = "data/photo"; // zip 파일 저장 경로
    private static final String BACKGROUND_DIR = "data/background"; // 단체사진 배경 저장 경로

    @Autowired
    public PhotoService(LessonMaterialRepository lessonMaterialRepository,
                        LessonRepository lessonRepository,
                        RestTemplate restTemplate,
                        AmazonS3 amazonS3) {
        this.lessonMaterialRepository = lessonMaterialRepository;
        this.lessonRepository = lessonRepository;
        this.restTemplate = restTemplate;
        this.amazonS3 = amazonS3;
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
     * AI 서버에 배경 생성 요청
     */
    public String createBackground(Long lessonMaterialId) {

        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() ->
                        new EntityNotFoundException("lessonMaterialId로 수업을 찾을 수 없습니다. lessonMaterial"));

        // 단체사진 예외처리
        if (lessonMaterial.getBackgroundUrl() != null) {
            return "단체 사진 배경이 이미 있습니다.";
        }

        // AI 서버로 보낼 요청 데이터 생성
        BackgroundRequestDto backgroundRequestDto = new BackgroundRequestDto(
                lessonMaterial.getBookContents(),
                lessonMaterialId
        );

        try {
            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<BackgroundRequestDto> requestEntity = new HttpEntity<>(backgroundRequestDto, headers);

            // AI 서버에 요청 보내기
            ResponseEntity<String> aiResponse = restTemplate.exchange(
                    AIServerURL, HttpMethod.POST, requestEntity, String.class);

            log.info("ai요청 : {}", aiResponse.getBody());

            if (aiResponse.getStatusCode().is2xxSuccessful()) {
                return "AI서버로 정상적으로 요청을 보냈습니다.";
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI 서버에서 이미지 생성 실패. Status: " + aiResponse.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 서버와 통신 중 문제가 발생했습니다.", e);
        }
    }


    /**
     * 배경 이미지 저장
     */
    @Transactional
    public String saveBackground(Long lessonMaterialId, MultipartFile backgroundImg) {

        // 로컬 저장
        /* try {
            String backgroundImgName = UUID.randomUUID().toString() + ".png"; // 사진 이름 생성
            Path filePath = Paths.get(BACKGROUND_DIR, backgroundImgName).normalize();

            if (!filePath.startsWith(Paths.get(BACKGROUND_DIR))) {
                throw new SecurityException("저장 경로를 찾을 수 없습니다.");
            }

            Files.write(filePath, backgroundImg.getBytes());

            lessonMaterial.setBackgroundUrl(createBackgroundFileUrl(backgroundImgName)); // photoUrl 추가

            lessonMaterialRepository.save(lessonMaterial);

            return "배경 이미지가 성공적으로 저장되었습니다.";
        } catch (IOException e) {
            throw new RuntimeException("배경 이미지 저장 중 오류가 발생했습니다.", e);
        }*/

        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() ->
                        new EntityNotFoundException("lessonMaterialId(" + lessonMaterialId + ")로 수업자료를 찾을 수 없습니다."));

        try {

            // 배경사진 이름 설정
            String backgroundImgName = "background/" + UUID.randomUUID().toString() + ".png"; // S3에 저장할 파일 이름 (폴더 포함)

            // 메타 데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(backgroundImg.getContentType()); // 객체 콘덴트 타입설정
            metadata.setContentLength(backgroundImg.getSize()); // 객체 크기 설정

            // S3에 업로드
            amazonS3.putObject(bucketName, backgroundImgName, backgroundImg.getInputStream(), metadata);

            // S3 URL 생성
            String s3Url = amazonS3.getUrl(bucketName, backgroundImgName).toString();

            // S3주소로 배경 저장
            lessonMaterial.setBackgroundUrl(s3Url);
            lessonMaterialRepository.save(lessonMaterial);

            return "배경 이미지가 S3에 성공적으로 저장되었습니다. URL: " + s3Url;

        } catch (IOException e) {
            throw new RuntimeException("S3에 배경 이미지를 업로드하는 중 오류가 발생했습니다.", e);
        }
    }


    /**
     * 배경사진 호출
     */
    public String getPhotoBackgroundUrl(Long lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() ->
                        new EntityNotFoundException("lessonId로 수업을 조회할 수 없습니다."));

        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lesson.getLessonMaterialId())
                .orElseThrow(() ->
                        new EntityNotFoundException("lessonMaterialId로 수업자료를 조회할 수 없습니다."));

        return lessonMaterial.getBackgroundUrl();
    }


    /**
     * 단체사진 저장
     */
    @Transactional
    public String savePhoto(Long lessonId, MultipartFile groupPhoto) {
        // 로컬 저장
        /*
        try {
            String photoName = UUID.randomUUID().toString() + ".png"; // 사진 이름 생성
            Path photoPath = Paths.get(PHOTO_DIR, photoName); // 파일 저장 경로 생성

            if (!photoPath.normalize().startsWith(Paths.get(PHOTO_DIR))) {
                throw new SecurityException("잘못된 파일 경로입니다.");
            }

            Files.write(photoPath, groupPhoto.getBytes()); // 파일 저장

            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

            lesson.setPhotoUrl(createPhotoFileUrl(photoName)); // photoUrl 추가

            lessonRepository.save(lesson);

        } catch (IOException e) {
            throw new IllegalArgumentException("사진을 저장하지 못했습니다.");
        }*/

        // S3 적용
        try {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId(" + lessonId + ")로 수업을 찾을 수 없습니다."));

            // 배경사진 이름 설정
            String groupPhotoName = "photo/" + UUID.randomUUID().toString() + ".png"; // S3에 저장할 파일 이름 (폴더 포함)

            // 메타 데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(groupPhoto.getContentType()); // 객체 콘덴트 타입설정
            metadata.setContentLength(groupPhoto.getSize()); // 객체 크기 설정

            // S3에 업로드
            amazonS3.putObject(bucketName, groupPhotoName, groupPhoto.getInputStream(), metadata);

            // S3 URL 생성
            String s3Url = amazonS3.getUrl(bucketName, groupPhotoName).toString();

            // S3주소로 단체사진 저장
            lesson.setPhotoUrl(s3Url);
            lessonRepository.save(lesson);

            return "배경 이미지가 S3에 성공적으로 저장되었습니다. URL: " + s3Url;

        } catch (IOException e) {
            throw new RuntimeException("S3에 배경 이미지를 업로드하는 중 오류가 발생했습니다.", e);
        }

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