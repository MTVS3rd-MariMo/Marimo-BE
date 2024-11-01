package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.dto.AvatarResponseDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.avatar.Animation;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.repository.AnimationRepository;
import com.todock.marimo.domain.lesson.repository.AvatarRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final RestTemplate restTemplate;

    private static final String DATA_DIR = "data"; // 파일 저장 경로
    private static final String ZIP_DIR = "data/zip"; // zip 파일 저장 경로
    private static final String UNZIP_DIR = "data/animation"; // 압축 해제된 파일 저장 경로


    @Autowired
    public AvatarService(AvatarRepository avatarRepository,
                         RestTemplate restTemplate) {
        this.avatarRepository = avatarRepository;
        this.restTemplate = restTemplate;
        initDirectories();
    }

    // 필요한 디렉토리를 초기화 하는 메서드
    public void initDirectories() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(ZIP_DIR));
            Files.createDirectories(Paths.get(UNZIP_DIR));
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
    }


    /**
     * img를 AI서버로 전송
     */
    @Transactional
    public void sendImgToAiServer(MultipartFile img) {

        try {
            // 1. AI 서버 URI 설정
            String AIServerUrI = "http://metaai2.iptime.org:62987/animation/";

            // 2. HttpHeaders 설정
            HttpHeaders headers = new HttpHeaders(); // Http 요청 헤더 생성
            headers.setContentType(MediaType.MULTIPART_FORM_DATA); // 컨텐츠 타입을 multipart/form-data 로 설정

            // 3. Img 파일을 멀티파트 형식으로 Wrapping
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 이미지 파일을 ByteArrayResource로 변환하여 요청 바디에 추가
            body.add("img", new ByteArrayResource(img.getBytes()) {
                @Override
                public String getFilename() {
                    return img.getOriginalFilename(); // 파일 이름 원본으로 설정
                }
            });

            // HTTP 요청 엔티티 생성 (헤더와 바디 포함)
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            // 5. AI 서버로 POST 요청을 보내고 응답을 받음
            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    AIServerUrI, request, byte[].class);

            // 고유한 zip 파일명 생성
            String zipFileName = UUID.randomUUID().toString() + ".zip";

            // zip 파일 저장 경로 생성
            Path zipPath = Paths.get(ZIP_DIR, zipFileName);

            if (!zipPath.normalize().startsWith(Paths.get(ZIP_DIR))) {
                throw new SecurityException("잘못된 파일 경로입니다.");
            }

            // zip 파일 저장
            Files.write(zipPath, response.getBody());

            // zip 파일 압축 해제 경로 설정 및 압축 해제 수행
            String unzipDirPath = Paths.get(UNZIP_DIR, zipFileName.replace(".zip", "")).toString();

            List<String> animationPaths = unzipFile(zipPath.toString(), unzipDirPath);

            unzipFile(zipPath.toString(), unzipDirPath);

        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생", e);
            throw new RuntimeException("파일 처리 실패", e);
        }
    }

    /**
     * 수업 id로 모든 아바타와 애니메이션 조회
     */
    public List<AvatarResponseDto> findByLessonId(Long lessonId) {
        return avatarRepository.findByLesson_LessonId(lessonId)
                .stream()
                .map(avatar -> new AvatarResponseDto(
                        avatar.getUserId(),
                        avatar.getAvatarImg(),
                        avatar.getAnimationList()
                ))
                .collect(Collectors.toList());
    }


    /**
     * zip 파일을 압축 해제하고 압축 해제된 파일들의 경로 목록을 반환
     */
    private List<String> unzipFile(String zipFilePath, String destDirectory) throws IOException {
        List<String> animationPaths = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path destPath = Paths.get(destDirectory, zipEntry.getName());
                if (!destPath.normalize().startsWith(Paths.get(destDirectory))) {
                    throw new SecurityException("Invalid zip entry path");
                }
                if (!zipEntry.isDirectory()) {
                    Files.createDirectories(destPath.getParent());
                    Files.copy(zis, destPath, StandardCopyOption.REPLACE_EXISTING);
                    animationPaths.add(destPath.toString());
                }
            }
        }
        return animationPaths;
    }

    // MultipartFile을 로컬 파일 시스템에 저장하고 저장된 경로를 반환
    private String saveImageFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
        Path destinationPath = Paths.get(DATA_DIR, fileName);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toString();
    }

    // 파일명에서 확장자를 추출
    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> "." + f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");
    }
}
