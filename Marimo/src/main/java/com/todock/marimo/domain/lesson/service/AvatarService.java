package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.dto.AvatarResponseDto;
import com.todock.marimo.domain.lesson.entity.avatar.Animation;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.repository.AvatarRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    // 클래스 내부에서 주입된 값을 사용하기 위해 추가
    //@Value("${server.host}")
    private String serverHost = "125.132.216.190";
    // 125.132.216.190:8202
    //@Value("${server.port}")
    private String serverPort = "8202";

    private final AvatarRepository avatarRepository;
    private final RestTemplate restTemplate;

    private static final String DATA_DIR = "data"; // 파일 저장 경로
    private static final String ZIP_DIR = "data/zip"; // zip 파일 저장 경로
    private static final String AVATAR_DIR = "data/avatar"; // avatar 파일 저장 경로

    @Autowired
    public AvatarService(AvatarRepository avatarRepository,
                         RestTemplate restTemplate) {
        this.avatarRepository = avatarRepository;
        this.restTemplate = restTemplate;
        initDirectories();
    }

    // 필요한 디렉토리를 초기화 하는 메서드
    public void initDirectories() {
        try { // 디렉토리 생성
            Files.createDirectories(Paths.get(DATA_DIR)); // 파일 저장 경로
            Files.createDirectories(Paths.get(ZIP_DIR)); // zip 파일 저장 경로
            Files.createDirectories(Paths.get(AVATAR_DIR)); // avatar 파일 저장 경로

        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
    }


    /**
     * img를 AI서버로 전송
     */
    @Transactional
    public AvatarResponseDto sendImgToAiServer(MultipartFile img) {

        try {
            // 1. AI 서버 URI 설정
            String AIServerUrI = "http://metaai2.iptime.org:64987/animation/";

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
            String unzipDirPath = Paths.get(AVATAR_DIR, zipFileName.replace(".zip", "")).toString();
            List<String> filePaths = unzipFile(zipPath.toString(), unzipDirPath);

            // 아바타 엔티티 생성 및 파일 저장
            Long userId = 1L; // 임시 userId 나중에 헤더에서 받은걸로 변경
            Avatar avatar = new Avatar();
            avatar.setUserId(userId);


            // 애니메이션 엔티티 연결
            List<Animation> animations = new ArrayList<>();
            for (String filePath : filePaths) {
                if (filePath.endsWith(".png")) {
                    avatar.setAvatarImg(createFileUrl(filePath)); // URL로 변환하여 avatarImg에 저장
                } else if (filePath.endsWith(".mp4")) {
                    Animation animation = new Animation();
                    animation.setAvatar(avatar);
                    animation.setAnimation(createFileUrl(filePath)); // URL로 변환하여 애니메이션에 저장
                    animations.add(animation);
                }
            }

            avatar.setAnimations(animations);

            // 8. 저장된 아바타와 애니메이션 정보로 AvatarResponseDto 생성
            avatar = avatarRepository.save(avatar);
            return new AvatarResponseDto(avatar.getUserId(), avatar.getAvatarImg(), avatar.getAnimations());


        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생", e);
            throw new RuntimeException("파일 처리 실패", e);
        }
    }

    /**
     * 유저 Id로 아바타 조회(이미지, 애니메이션)
     */
    public AvatarResponseDto findByUserId(Long lessonId, Long userId) {

        // 유저 ID로 아바타 조회
        Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(lessonId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저와 수업이 공통으로 있는 아바타를 찾을 수 없습니다: " + userId));

        // 애니메이션 목록 생성
        List<Animation> animations = avatar.getAnimations().stream()
                .map(animation -> {
                    // 각 애니메이션 경로를 URL로 변환
                    String animationUrl = createFileUrl(animation.getAnimation());
                    animation.setAnimation(animationUrl);
                    return animation;
                }).collect(Collectors.toList());

        // 아바타 이미지 URL로 변환
        String avatarImgUrl = createFileUrl(avatar.getAvatarImg());

        // AvatarResponseDto 생성 및 반환
        return new AvatarResponseDto(avatar.getUserId(), avatarImgUrl, animations);
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
                        avatar.getAnimations()
                ))
                .collect(Collectors.toList());
    }


    /**
     * zip 파일을 압축 해제하고 압축 해제된 파일들의 경로 목록을 반환
     */
    private List<String> unzipFile(String zipFilePath, String destDirectory) throws IOException {
        List<String> filePaths = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path destPath = Paths.get(destDirectory, zipEntry.getName()); // avatar 폴더에 압축 해제
                if (!destPath.normalize().startsWith(Paths.get(destDirectory))) {
                    throw new SecurityException("잘못된 zip 파일 경로입니다.");
                }
                if (!zipEntry.isDirectory()) {
                    Files.createDirectories(destPath.getParent());
                    Files.copy(zis, destPath, StandardCopyOption.REPLACE_EXISTING);
                    filePaths.add(destPath.toString());
                }
            }
        }
        return filePaths;
    }

    // MultipartFile을 로컬 파일 시스템에 저장하고 저장된 경로를 반환
    private String saveImageFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
        Path destinationPath = Paths.get(DATA_DIR, fileName);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toString();
    }

    /**
     * 파일명에서 확장자를 추출
     */
    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> "." + f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");
    }

    /**
     * 파일 확장자에 따라 다른 폴더에 저장
     */
    private String saveFileByType(MultipartFile file) throws IOException {
        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        String targetDir;

        // 파일 확장자에 따라 폴더를 구분
        if (fileExtension.equals(".png")) {
            targetDir = AVATAR_DIR;
        } else if (fileExtension.equals(".mp4")) {
            targetDir = "data/animation";
        } else {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다. PNG 또는 MP4 파일만 업로드 가능합니다.");
        }

        // 폴더가 없으면 생성
        Files.createDirectories(Paths.get(targetDir));

        // 파일을 저장할 경로를 설정
        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path destinationPath = Paths.get(targetDir, fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return destinationPath.toString();
    }

    /**
     * 파일 경로를 URL 형식으로 변환
     */
    private String createFileUrl(String filePath) {
        // 파일 경로에서 중복된 루트 디렉토리를 제거
        String relativePath = filePath.replace("\\", "/");
        relativePath = relativePath.replaceFirst("^data/avatar/", ""); // "data/avatar/" 제거
        return "http://" + serverHost + ":" + serverPort + "/data/avatar/" + relativePath;
    }


}
