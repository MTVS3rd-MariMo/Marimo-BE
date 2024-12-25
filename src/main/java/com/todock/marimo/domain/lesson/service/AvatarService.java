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

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final RestTemplate restTemplate;
    private final LessonRepository lessonRepository;
    private final AvatarRepository avatarRepository;

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
    }


    
    /**
     * 아마존 S3 아바타 저장 메서드
     */
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

}
