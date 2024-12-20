package com.todock.marimo.domain.lesson.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.todock.marimo.domain.lesson.dto.AvatarResponseDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.avatar.Animation;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.repository.AvatarRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

import java.io.ByteArrayInputStream;
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

    @Value("${external.api.odd-avatar-server-url}")
    private String AIOddAvatarServerURL;

    @Value("${external.api.even-avatar-server-url}")
    private String AIEvenAvatarServerURL;

    @Value("${external.port.server-host}")
    private String serverHost;

    @Value("${external.port.external-port}")
    private String serverPort;

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final RestTemplate restTemplate;
    private final LessonRepository lessonRepository;
    private final AvatarRepository avatarRepository;

    private static final String DATA_DIR = "data"; // 파일 저장 경로
    private static final String ZIP_DIR = "data/zip"; // zip 파일 저장 경로
    private static final String AVATAR_DIR = "data/avatar"; // avatar 파일 저장 경로

    @Autowired
    public AvatarService(
            AmazonS3 amazonS3,
            LessonRepository lessonRepository
            , AvatarRepository avatarRepository
            , RestTemplate restTemplate) {

        this.amazonS3 = amazonS3;
        this.lessonRepository = lessonRepository;
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
     * img를 AI서버로 전송 - 아바타 더미데이터 사용
     */
    @Transactional
    public AvatarResponseDto dummyAvatar(Long userId, Long lessonId, MultipartFile img) {

        log.info("\n\n아바타 생성 테스트 : lessonId = {}, userId = {}\n\n", lessonId, userId);

        // 시연용 더미 코드
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

        AvatarResponseDto avatarResponseDto;

        if (userId == 1L) { // 만복이
            Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(2L, userId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId : 1, userId 1로 빨간모자 아바타를 찾을 수 없습니다."));

            // 기존 Animation을 복사하여 새로운 Animation 생성
            List<Animation> newAnimations = new ArrayList<>();
            for (Animation animation : avatar.getAnimations()) {
                Animation newAnimation = new Animation();
                newAnimation.setAnimation(animation.getAnimation()); // 애니메이션 데이터 복사
                newAnimation.setAvatar(null); // 새로운 아바타와 연결 예정
                newAnimations.add(newAnimation);
            }

            // 새로운 Avatar 생성 및 설정
            Avatar newAvatar = new Avatar();
            newAvatar.setLesson(lesson); // 새로운 lesson 설정
            newAvatar.setUserId(userId); // 새로운 userId 설정
            newAvatar.setAvatarImg(avatar.getAvatarImg()); // 기존 아바타 이미지 복사
            newAvatar.setCharacter(avatar.getCharacter()); // 역할 복사
            newAvatar.setAnimations(newAnimations); // 복사한 애니메이션 설정

            // 새로운 Animation에 Avatar 연결
            for (Animation animation : newAnimations) {
                animation.setAvatar(newAvatar); // 새 아바타와 연결
            }

            // 데이터 저장
            avatarRepository.save(newAvatar); // 새로운 아바타와 애니메이션 저장
            lesson.getAvatarList().add(newAvatar); // lesson에 새로운 아바타 추가
            lessonRepository.save(lesson);

            avatarResponseDto = new AvatarResponseDto(newAvatar.getUserId(), newAvatar.getAvatarImg(), newAvatar.getAnimations());
            avatar.setLesson(lessonRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("lessonId 2로 변경 실패")));
            return avatarResponseDto;

        } else if (userId == 2L) { // 만복이엄마
            Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(2L, userId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId : 1, userId 2로 할머니 아바타를 찾을 수 없습니다."));

            // 기존 Animation을 복사하여 새로운 Animation 생성
            List<Animation> newAnimations = new ArrayList<>();
            for (Animation animation : avatar.getAnimations()) {
                Animation newAnimation = new Animation();
                newAnimation.setAnimation(animation.getAnimation()); // 애니메이션 데이터 복사
                newAnimation.setAvatar(null); // 새로운 아바타와 연결 예정
                newAnimations.add(newAnimation);
            }

            // 새로운 Avatar 생성 및 설정
            Avatar newAvatar = new Avatar();
            newAvatar.setLesson(lesson); // 새로운 lesson 설정
            newAvatar.setUserId(userId); // 새로운 userId 설정
            newAvatar.setAvatarImg(avatar.getAvatarImg()); // 기존 아바타 이미지 복사
            newAvatar.setCharacter(avatar.getCharacter()); // 역할 복사
            newAvatar.setAnimations(newAnimations); // 복사한 애니메이션 설정

            // 새로운 Animation에 Avatar 연결
            for (Animation animation : newAnimations) {
                animation.setAvatar(newAvatar); // 새 아바타와 연결
            }

            // 데이터 저장
            avatarRepository.save(newAvatar); // 새로운 아바타와 애니메이션 저장
            lesson.getAvatarList().add(newAvatar); // lesson에 새로운 아바타 추가
            lessonRepository.save(lesson);

            avatarResponseDto = new AvatarResponseDto(newAvatar.getUserId(), newAvatar.getAvatarImg(), newAvatar.getAnimations());
            avatar.setLesson(lessonRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("lessonId 1로 변경 실패")));
            return avatarResponseDto;

        } else if (userId == 3L) { // 선생님
            Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(2L, userId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId : 1, userId 3로 늑대 아바타를 찾을 수 없습니다."));

            // 기존 Animation을 복사하여 새로운 Animation 생성
            List<Animation> newAnimations = new ArrayList<>();
            for (Animation animation : avatar.getAnimations()) {
                Animation newAnimation = new Animation();
                newAnimation.setAnimation(animation.getAnimation()); // 애니메이션 데이터 복사
                newAnimation.setAvatar(null); // 새로운 아바타와 연결 예정
                newAnimations.add(newAnimation);
            }

            // 새로운 Avatar 생성 및 설정
            Avatar newAvatar = new Avatar();
            newAvatar.setLesson(lesson); // 새로운 lesson 설정
            newAvatar.setUserId(userId); // 새로운 userId 설정
            newAvatar.setAvatarImg(avatar.getAvatarImg()); // 기존 아바타 이미지 복사
            newAvatar.setCharacter(avatar.getCharacter()); // 역할 복사
            newAvatar.setAnimations(newAnimations); // 복사한 애니메이션 설정

            // 새로운 Animation에 Avatar 연결
            for (Animation animation : newAnimations) {
                animation.setAvatar(newAvatar); // 새 아바타와 연결
            }

            // 데이터 저장
            avatarRepository.save(newAvatar); // 새로운 아바타와 애니메이션 저장
            lesson.getAvatarList().add(newAvatar); // lesson에 새로운 아바타 추가
            lessonRepository.save(lesson);

            avatarResponseDto = new AvatarResponseDto(newAvatar.getUserId(), newAvatar.getAvatarImg(), newAvatar.getAnimations());
            avatar.setLesson(lessonRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("lessonId 1으로 변경 실패")));
            return avatarResponseDto;

        } else if (userId == 4L) { // 은지
            Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(2L, userId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId : 1, userId 2로 사냥꾼 아바타를 찾을 수 없습니다."));

            // 기존 Animation을 복사하여 새로운 Animation 생성
            List<Animation> newAnimations = new ArrayList<>();
            for (Animation animation : avatar.getAnimations()) {
                Animation newAnimation = new Animation();
                newAnimation.setAnimation(animation.getAnimation()); // 애니메이션 데이터 복사
                newAnimation.setAvatar(null); // 새로운 아바타와 연결 예정
                newAnimations.add(newAnimation);
            }

            // 새로운 Avatar 생성 및 설정
            Avatar newAvatar = new Avatar();
            newAvatar.setLesson(lesson); // 새로운 lesson 설정
            newAvatar.setUserId(userId); // 새로운 userId 설정
            newAvatar.setAvatarImg(avatar.getAvatarImg()); // 기존 아바타 이미지 복사
            newAvatar.setCharacter(avatar.getCharacter()); // 역할 복사
            newAvatar.setAnimations(newAnimations); // 복사한 애니메이션 설정

            // 새로운 Animation에 Avatar 연결
            for (Animation animation : newAnimations) {
                animation.setAvatar(newAvatar); // 새 아바타와 연결
            }

            // 데이터 저장
            avatarRepository.save(newAvatar); // 새로운 아바타와 애니메이션 저장
            lesson.getAvatarList().add(newAvatar); // lesson에 새로운 아바타 추가
            lessonRepository.save(lesson);

            avatarResponseDto = new AvatarResponseDto(newAvatar.getUserId(), newAvatar.getAvatarImg(), newAvatar.getAnimations());
            avatar.setLesson(lessonRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("lessonId 1로 변경 실패")));
            return avatarResponseDto;

        }

        return null;
    }


    /**
     * img를 AI서버로 전송 - 로컬저장
     */
    @Transactional
    public AvatarResponseDto sendImgToAiServer(Long userId, Long lessonId, MultipartFile img) {

        log.info("\n\n아바타 생성 테스트 : lessonId = {}, userId = {}\n\n", lessonId, userId);

        try {

            // 1. HttpHeaders 설정
            HttpHeaders headers = new HttpHeaders(); // Http 요청 헤더 생성
            headers.setContentType(MediaType.MULTIPART_FORM_DATA); // 컨텐츠 타입을 multipart/form-data 로 설정

            // 2. Img 파일을 멀티파트 형식으로 Wrapping
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 3. 이미지 파일을 ByteArrayResource로 변환하여 요청 바디에 추가
            body.add("img", new ByteArrayResource(img.getBytes()) {
                @Override
                public String getFilename() {
                    return img.getOriginalFilename(); // 파일 이름 원본으로 설정
                }
            });

            // 4. HTTP 요청 엔티티 생성 (헤더와 바디 포함)
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> AIResponse;
            // 5. AI 서버로 POST 요청을 보내고 응답을 받음 - 유저Id를 홀수, 짝수 일때 나눠서 AI서버로 요청 보냄
            if (userId % 2 != 0) {
                log.info("userId : {}는 홀수라서 {}로 보냈습니다.", userId, AIEvenAvatarServerURL);
                AIResponse = restTemplate.postForEntity(
                        AIEvenAvatarServerURL, request, byte[].class);
            } else {
                log.info("userId : {}는 짝수라서 {}로 보냈습니다.", userId, AIEvenAvatarServerURL);
                AIResponse = restTemplate.postForEntity(
                        AIEvenAvatarServerURL, request, byte[].class);
            }

            // 고유한 zip 파일명 생성
            String zipFileName = UUID.randomUUID().toString() + ".zip";

            // zip 파일 저장 경로 생성
            Path zipPath = Paths.get(ZIP_DIR, zipFileName);

            if (!zipPath.normalize().startsWith(Paths.get(ZIP_DIR))) {
                throw new SecurityException("잘못된 파일 경로입니다.");
            }

            // zip 파일 저장
            Files.write(zipPath, AIResponse.getBody());

            // zip 파일 압축 해제 경로 설정 및 압축 해제 수행
            String unzipDirPath = Paths.get(AVATAR_DIR, zipFileName.replace(".zip", "")).toString();
            List<String> filePaths = unzipFile(zipPath.toString(), unzipDirPath);

            // 아바타 엔티티 생성 및 파일 저장
            Avatar avatar = new Avatar();
            // 나머지 아바타들도 통신할대는 Lesson 타입 지정할 것
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));
            avatar.setUserId(userId);
            avatar.setLesson(lesson);

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

            // 유저에게 반환
            return new AvatarResponseDto(avatar.getUserId(), avatar.getAvatarImg(), avatar.getAnimations());

        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생", e);
            throw new RuntimeException("파일 처리 실패", e);
        }
    }


    /**
     * img를 AI서버로 전송 - 로컬저장
     */
    @Transactional
    public AvatarResponseDto halfDummyAvatar(Long userId, Long lessonId, MultipartFile img) {

        log.info("\n\n아바타 생성 테스트 : lessonId = {}, userId = {}\n\n", lessonId, userId);

        if (userId == 2L) {

            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException(lessonId + "로 수업을 찾을 수 없습니다."));

            AvatarResponseDto avatarResponseDto;

            Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(2L, userId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId : 2, userId 2으로 왕자 아바타를 찾을 수 없습니다."));
            // 기존 Animation을 복사하여 새로운 Animation 생성
            List<Animation> newAnimations = new ArrayList<>();
            for (Animation animation : avatar.getAnimations()) {
                Animation newAnimation = new Animation();
                newAnimation.setAnimation(animation.getAnimation()); // 애니메이션 데이터 복사
                newAnimation.setAvatar(null); // 새로운 아바타와 연결 예정
                newAnimations.add(newAnimation);
            }

            // 새로운 Avatar 생성 및 설정
            Avatar newAvatar = new Avatar();
            newAvatar.setLesson(lesson); // 새로운 lesson 설정
            newAvatar.setUserId(userId); // 새로운 userId 설정
            newAvatar.setAvatarImg(avatar.getAvatarImg()); // 기존 아바타 이미지 복사
            newAvatar.setCharacter(avatar.getCharacter()); // 역할 복사
            newAvatar.setAnimations(newAnimations); // 복사한 애니메이션 설정

            // 새로운 Animation에 Avatar 연결
            for (Animation animation : newAnimations) {
                animation.setAvatar(newAvatar); // 새 아바타와 연결
            }

            // 데이터 저장
            avatarRepository.save(newAvatar); // 새로운 아바타와 애니메이션 저장
            lesson.getAvatarList().add(newAvatar); // lesson에 새로운 아바타 추가
            lessonRepository.save(lesson);

            avatarResponseDto = new AvatarResponseDto(newAvatar.getUserId(), newAvatar.getAvatarImg(), newAvatar.getAnimations());
            avatar.setLesson(lessonRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("lessonId 2로 변경 실패")));
            return avatarResponseDto;

        } else if (userId == 3L) {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException(lessonId + "로 수업을 찾을 수 없습니다."));

            AvatarResponseDto avatarResponseDto;

            Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(2L, userId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId : 2, userId 4로  바다 마녀 아바타를 찾을 수 없습니다."));

            // 기존 Animation을 복사하여 새로운 Animation 생성
            List<Animation> newAnimations = new ArrayList<>();
            for (Animation animation : avatar.getAnimations()) {
                Animation newAnimation = new Animation();
                newAnimation.setAnimation(animation.getAnimation()); // 애니메이션 데이터 복사
                newAnimation.setAvatar(null); // 새로운 아바타와 연결 예정
                newAnimations.add(newAnimation);
            }

            // 새로운 Avatar 생성 및 설정
            Avatar newAvatar = new Avatar();
            newAvatar.setLesson(lesson); // 새로운 lesson 설정
            newAvatar.setUserId(userId); // 새로운 userId 설정
            newAvatar.setAvatarImg(avatar.getAvatarImg()); // 기존 아바타 이미지 복사
            newAvatar.setCharacter(avatar.getCharacter()); // 역할 복사
            newAvatar.setAnimations(newAnimations); // 복사한 애니메이션 설정

            // 새로운 Animation에 Avatar 연결
            for (Animation animation : newAnimations) {
                animation.setAvatar(newAvatar); // 새 아바타와 연결
            }

            // 데이터 저장
            avatarRepository.save(newAvatar); // 새로운 아바타와 애니메이션 저장
            lesson.getAvatarList().add(newAvatar); // lesson에 새로운 아바타 추가
            lessonRepository.save(lesson);

            avatarResponseDto = new AvatarResponseDto(newAvatar.getUserId(), newAvatar.getAvatarImg(), newAvatar.getAnimations());
            avatar.setLesson(lessonRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("lessonId 1으로 변경 실패")));
            return avatarResponseDto;

        } else if (userId == 4L) {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException(lessonId + "로 수업을 찾을 수 없습니다."));

            AvatarResponseDto avatarResponseDto;

            Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(2L, userId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId : 2, userId 4로  바다 왕 아바타를 찾을 수 없습니다."));

            // 기존 Animation을 복사하여 새로운 Animation 생성
            List<Animation> newAnimations = new ArrayList<>();
            for (Animation animation : avatar.getAnimations()) {
                Animation newAnimation = new Animation();
                newAnimation.setAnimation(animation.getAnimation()); // 애니메이션 데이터 복사
                newAnimation.setAvatar(null); // 새로운 아바타와 연결 예정
                newAnimations.add(newAnimation);
            }

            // 새로운 Avatar 생성 및 설정
            Avatar newAvatar = new Avatar();
            newAvatar.setLesson(lesson); // 새로운 lesson 설정
            newAvatar.setUserId(userId); // 새로운 userId 설정
            newAvatar.setAvatarImg(avatar.getAvatarImg()); // 기존 아바타 이미지 복사
            newAvatar.setCharacter(avatar.getCharacter()); // 역할 복사
            newAvatar.setAnimations(newAnimations); // 복사한 애니메이션 설정

            // 새로운 Animation에 Avatar 연결
            for (Animation animation : newAnimations) {
                animation.setAvatar(newAvatar); // 새 아바타와 연결
            }

            // 데이터 저장
            avatarRepository.save(newAvatar); // 새로운 아바타와 애니메이션 저장
            lesson.getAvatarList().add(newAvatar); // lesson에 새로운 아바타 추가
            lessonRepository.save(lesson);

            avatarResponseDto = new AvatarResponseDto(newAvatar.getUserId(), newAvatar.getAvatarImg(), newAvatar.getAnimations());
            avatar.setLesson(lessonRepository.findById(2L).orElseThrow(() -> new EntityNotFoundException("lessonId 1으로 변경 실패")));
            return avatarResponseDto;
        } else {

            try {

                // 1. HttpHeaders 설정
                HttpHeaders headers = new HttpHeaders(); // Http 요청 헤더 생성
                headers.setContentType(MediaType.MULTIPART_FORM_DATA); // 컨텐츠 타입을 multipart/form-data 로 설정

                // 2. Img 파일을 멀티파트 형식으로 Wrapping
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

                // 3. 이미지 파일을 ByteArrayResource로 변환하여 요청 바디에 추가
                body.add("img", new ByteArrayResource(img.getBytes()) {
                    @Override
                    public String getFilename() {
                        return img.getOriginalFilename(); // 파일 이름 원본으로 설정
                    }
                });

                // 4. HTTP 요청 엔티티 생성 (헤더와 바디 포함)
                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

                ResponseEntity<byte[]> AIResponse;
                // 5. AI 서버로 POST 요청을 보내고 응답을 받음 - 유저Id를 홀수, 짝수 일때 나눠서 AI서버로 요청 보냄
                if (userId % 2 != 0) {
                    log.info("userId : {}는 홀수라서 {}로 보냈습니다.", userId, AIEvenAvatarServerURL);
                    AIResponse = restTemplate.postForEntity(
                            AIEvenAvatarServerURL, request, byte[].class);
                } else {
                    log.info("userId : {}는 짝수라서 {}로 보냈습니다.", userId, AIEvenAvatarServerURL);
                    AIResponse = restTemplate.postForEntity(
                            AIEvenAvatarServerURL, request, byte[].class);
                }

                // 고유한 zip 파일명 생성
                String zipFileName = UUID.randomUUID().toString() + ".zip";

                // zip 파일 저장 경로 생성
                Path zipPath = Paths.get(ZIP_DIR, zipFileName);

                if (!zipPath.normalize().startsWith(Paths.get(ZIP_DIR))) {
                    throw new SecurityException("잘못된 파일 경로입니다.");
                }

                // zip 파일 저장
                Files.write(zipPath, AIResponse.getBody());

                // zip 파일 압축 해제 경로 설정 및 압축 해제 수행
                String unzipDirPath = Paths.get(AVATAR_DIR, zipFileName.replace(".zip", "")).toString();
                List<String> filePaths = unzipFile(zipPath.toString(), unzipDirPath);

                // 아바타 엔티티 생성 및 파일 저장
                Avatar avatar = new Avatar();
                // 나머지 아바타들도 통신할대는 Lesson 타입 지정할 것
                Lesson lesson = lessonRepository.findById(lessonId)
                        .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));
                avatar.setUserId(userId);
                avatar.setLesson(lesson);

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

                // 유저에게 반환
                return new AvatarResponseDto(avatar.getUserId(), avatar.getAvatarImg(), avatar.getAnimations());

            } catch (Exception e) {
                log.error("파일 처리 중 오류 발생", e);
                throw new RuntimeException("파일 처리 실패", e);
            }
        }
    }


    /**
     * 유저 Id로 아바타 조회(이미지, 애니메이션)
     */
    public AvatarResponseDto findByUserId(Long lessonId, Long userId) {

        // 유저 ID로 아바타 조회
        Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(lessonId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("userId와 lessonId가 공통으로 있는 아바타를 찾을 수 없습니다"));

        // 애니메이션 목록 생성 로컬
        /*
        List<Animation> animations = avatar.getAnimations().stream()
                .map(animation -> {
                    // 각 애니메이션 경로를 URL로 변환
                    String animationUrl = createFileUrl(animation.getAnimation());
                    animation.setAnimation(animationUrl);
                    return animation;
                }).collect(Collectors.toList());

        // 아바타 이미지 URL로 변환
        String avatarImgUrl = createFileUrl(avatar.getAvatarImg());
        */

        // AvatarResponseDto 생성 및 반환
        return new AvatarResponseDto(avatar.getUserId(), avatar.getAvatarImg(), avatar.getAnimations());
    }


    /**
     * ===============================================================
     *                           검증, 변환
     * ===============================================================
     */


    /**
     * zip 파일을 압축 해제하고 압축 해제된 파일들의 경로 목록을 반환
     */
    private List<String> unzipFile(String zipFilePath, String destDirectory) throws IOException {

        // 압축 해제된 파일들의 경로를 저장할 리스트 생성
        List<String> filePaths = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {

            ZipEntry zipEntry; // zip 파일 관리 클래스

            // normalize()를 통해 경로를 표준화하고, destDirectory로 시작하는지 확인
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path destPath = Paths.get(destDirectory, zipEntry.getName()); // avatar 폴더에 압축 해제

                if (!destPath.normalize().startsWith(Paths.get(destDirectory))) {
                    throw new SecurityException("잘못된 zip 파일 경로입니다.");
                }

                // 엔트리가 디렉토리가 아닌 경우에만 파일을 처리한다
                if (!zipEntry.isDirectory()) {
                    Files.createDirectories(destPath.getParent());
                    Files.copy(zis, destPath, StandardCopyOption.REPLACE_EXISTING);
                    filePaths.add(destPath.toString());
                }
            }

            return filePaths;

        } catch (Exception e) {

            throw new IOException("파일 처리 중 오류 발생(" + zipFilePath + ")", e);
        }
    }


    /**
     * MultipartFile을 로컬 파일 시스템에 저장하고 저장된 경로를 반환
     */
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

    /**
     * 아마존 S3 아바타 저장 메서드
     */
    /*
    @Transactional
    public AvatarResponseDto saveAwsAvatar(Long userId, Long lessonId, MultipartFile img) {

        log.info("\n\n아바타 생성 테스트 : lessonId = {}, userId = {}\n\n", lessonId, userId);

        try {
            // 1. AI 서버 요청
            byte[] zipBytes = sendImgToAiServer(userId, img);

            // 2. ZIP 파일을 S3에 저장
            String zipFileName = UUID.randomUUID().toString() + ".zip";
            String zipS3Key = "zip/" + zipFileName;
            uploadToS3(zipS3Key, zipBytes);

            // 3. ZIP 파일 압축 해제 및 S3 업로드
            String uuidFolder = UUID.randomUUID().toString();
            String avatarS3Prefix = "avatars/" + uuidFolder + "/";
            List<String> fileUrls = extractAndUploadFilesToS3(zipS3Key, avatarS3Prefix);

            // 4. 아바타 및 애니메이션 데이터 처리
            Avatar avatar = createAvatarEntity(userId, lessonId, fileUrls);

            // 5. DB에 저장
            avatar = avatarRepository.save(avatar);

            return new AvatarResponseDto(avatar.getUserId(), avatar.getAvatarImg(), avatar.getAnimations());
        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생", e);
            throw new RuntimeException("파일 처리 실패", e);
        }
    }
    // ai 서버 전송
    private byte[] sendImgToAiServer(Long userId, MultipartFile img) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("img", new ByteArrayResource(img.getBytes()) {
            @Override
            public String getFilename() {
                return img.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        String serverUrl = (userId % 2 != 0) ? AIEvenAvatarServerURL : AIOddAvatarServerURL;
        log.info("userId : {} 서버 URL : {}", userId, serverUrl);

        ResponseEntity<byte[]> response = restTemplate.postForEntity(serverUrl, request, byte[].class);
        return response.getBody();
    }
    // aws에 올리기
    private void uploadToS3(String key, byte[] data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        amazonS3.putObject(new PutObjectRequest(bucketName, key, new ByteArrayInputStream(data), metadata));
        log.info("파일이 S3에 업로드되었습니다. 경로: {}", key);
    }
    // 압축 해제 후 애니메이션 올리기
    private List<String> extractAndUploadFilesToS3(String zipS3Key, String avatarS3Prefix) throws IOException {
        S3Object zipObject = amazonS3.getObject(bucketName, zipS3Key);

        List<String> fileUrls = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(zipObject.getObjectContent())) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    String fileName = zipEntry.getName();
                    String s3Key = avatarS3Prefix + fileName;

                    // 파일을 S3에 직접 업로드
                    byte[] fileData = zis.readAllBytes();
                    uploadToS3(s3Key, fileData);

                    // 업로드된 파일 URL 생성
                    String fileUrl = amazonS3.getUrl(bucketName, s3Key).toString();
                    fileUrls.add(fileUrl);
                    log.info("압축 해제 및 업로드 완료: {}", fileUrl);
                }
            }
        }
        return fileUrls;
    }
    // 아바타 저장
    private Avatar createAvatarEntity(Long userId, Long lessonId, List<String> fileUrls) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

        Avatar avatar = new Avatar();
        avatar.setUserId(userId);
        avatar.setLesson(lesson);

        List<Animation> animations = new ArrayList<>();
        for (String fileUrl : fileUrls) {
            if (fileUrl.endsWith(".png")) {
                avatar.setAvatarImg(fileUrl);
            } else if (fileUrl.endsWith(".mp4")) {
                Animation animation = new Animation();
                animation.setAvatar(avatar);
                animation.setAnimation(fileUrl);
                animations.add(animation);
            }
        }
        avatar.setAnimations(animations);

        return avatar;
    }
    */

}
