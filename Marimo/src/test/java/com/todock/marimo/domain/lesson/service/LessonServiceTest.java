package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.avatar.Animation;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.repository.AnimationRepository;
import com.todock.marimo.domain.lesson.repository.AvatarRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private AvatarRepository avatarRepository;
    @Mock
    private AnimationRepository animationRepository;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AvatarService avatarService;

    private MockMultipartFile mockImage;
    private byte[] mockZipData;

    @BeforeEach
    void setUp() throws IOException {
        // 테스트용 이미지 파일 생성
        mockImage = new MockMultipartFile(
                "test-image",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // 테스트용 ZIP 파일 생성
        mockZipData = createMockZipFile();
    }

    @Test
    @DisplayName("이미지 업로드 및 ZIP 파일 처리 테스트")
    void testSendImgToAiServer() throws IOException {
        // Given
        Long userId = 1L;
        Long lessonId = 1L;
        Lesson mockLesson = new Lesson();
        Avatar mockAvatar = new Avatar();

        // Mock responses
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(mockLesson));
        when(avatarRepository.save(any(Avatar.class))).thenReturn(mockAvatar);
        when(restTemplate.postForEntity(anyString(), any(), eq(byte[].class)))
                .thenReturn(ResponseEntity.ok(mockZipData));

        // When
        assertDoesNotThrow(() -> avatarService.sendImgToAiServer(mockImage, userId, lessonId));

        // Then
        verify(lessonRepository).findById(lessonId);
        verify(avatarRepository).save(any(Avatar.class));
        verify(animationRepository, times(2)).save(any(Animation.class)); // 두 개의 애니메이션 파일 저장 확인
    }

    @Test
    @DisplayName("잘못된 ZIP 파일 처리 테스트")
    void testInvalidZipFile() {
        // Given
        Long userId = 1L;
        Long lessonId = 1L;
        byte[] invalidZipData = "invalid zip content".getBytes();

        when(restTemplate.postForEntity(anyString(), any(), eq(byte[].class)))
                .thenReturn(ResponseEntity.ok(invalidZipData));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> avatarService.sendImgToAiServer(mockImage, userId, lessonId));
    }

    private byte[] createMockZipFile() throws IOException {
        Path tempDir = Files.createTempDirectory("test-zip");
        Path zipPath = tempDir.resolve("test.zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            // 첫 번째 애니메이션 파일 추가
            ZipEntry entry1 = new ZipEntry("animation1.gif");
            zos.putNextEntry(entry1);
            zos.write("animation1 content".getBytes());
            zos.closeEntry();

            // 두 번째 애니메이션 파일 추가
            ZipEntry entry2 = new ZipEntry("animation2.gif");
            zos.putNextEntry(entry2);
            zos.write("animation2 content".getBytes());
            zos.closeEntry();
        }

        byte[] zipContent = Files.readAllBytes(zipPath);
        Files.delete(zipPath);
        Files.delete(tempDir);
        return zipContent;
    }

    @Test
    @DisplayName("디렉토리 생성 테스트")
    void testDirectoryInitialization() {
        // Given & When
        avatarService.initDirectories();

        // Then
        assertTrue(Files.exists(Paths.get("data")));
        assertTrue(Files.exists(Paths.get("data/zip")));
        assertTrue(Files.exists(Paths.get("data/animation")));
    }

    @Test
    @DisplayName("Lesson not found 예외 테스트")
    void testLessonNotFound() {
        // Given
        Long userId = 1L;
        Long lessonId = 999L;

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());
        when(restTemplate.postForEntity(anyString(), any(), eq(byte[].class)))
                .thenReturn(ResponseEntity.ok(mockZipData));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> avatarService.sendImgToAiServer(mockImage, userId, lessonId));
    }
}