package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.dto.LessonOpenQuestionRequestDto;
import com.todock.marimo.domain.lesson.dto.ParticipantListDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.Participant;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.lessonmaterial.repository.ParticipantRepository;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


/**
 * 수업 서비스 클래스: 수업 생성, 참가자 관리, 열린 질문 저장 등의 기능을 담당
 */
@Slf4j
@Service
public class LessonService {

    // 클래스 내부에서 주입된 값을 사용하기 위해 추가
    //@Value("${server.host}")
    private String serverHost = "211.250.74.75";
    // 125.132.216.190:8202
    //@Value("${server.port}")
    private String serverPort = "8202";

    private final ParticipantRepository participantRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    private static final String PHOTO_DIR = "data/photo"; // zip 파일 저장 경로

    @Autowired
    public LessonService(LessonMaterialRepository lessonMaterialRepository
            , ParticipantRepository participantRepository
            , LessonRepository lessonRepository
            , UserRepository userRepository) {
        this.participantRepository = participantRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        initDirectories();
    }

    // 필요한 디렉토리를 초기화 하는 메서드
    public void initDirectories() {
        try { // 디렉토리 생성
            Files.createDirectories(Paths.get(PHOTO_DIR)); // PHOTO 파일 저장 경로

        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
    }


    /**
     * 수업 생성 - lessonMaterialId를 받고 수업 자료와 LessonId 및 LessonMaterial 반환
     */
    public Long createLesson(Long userId, Long lessonMaterialId) {

        // lessonMaterial 조회

        // Lesson 생성 및 설정
        Lesson newlesson = new Lesson(// 수업에 lessonMaterial 넣어서 수업으로 찾을 수 있게 함
                lessonMaterialId
        );
        lessonRepository.save(newlesson); // 저장 후 ID가 생성됨
        Long lessonId = newlesson.getLessonId(); // lessonId 추출
        log.info("lessonId : {}", lessonId);
        // 선생님을 참가자 목록에 추가하기
        updateUserByLessonId(userId, lessonId);

//        // 결과를 저장할 lessonResult 객체 생성 및 lessonId 연결
//        LessonResult lessonResult = new LessonResult();
//        lessonResult.setLessonId(lessonId);
//
//        // openQuestions 변환
//        List<OpenQuestionResponseDto> openQuestions = lessonMaterial.getOpenQuestionList().stream()
//                .map(openQuestion -> new OpenQuestionResponseDto(openQuestion.getQuestion()))
//                .toList();
//
//        // quizzes 변환
//        List<QuizDto> quizzes = lessonMaterial.getQuizList().stream()
//                .map(quiz -> new QuizDto(
//                        quiz.getQuizId(),
//                        quiz.getQuestion(),
//                        quiz.getAnswer(),
//                        quiz.getChoices1(),
//                        quiz.getChoices2(),
//                        quiz.getChoices3(),
//                        quiz.getChoices4()
//                ))
//                .toList();
//
//        // lessonRoles 변환
//        List<LessonRoleDto> lessonRoles = lessonMaterial.getLessonRoleList().stream()
//                .map(role -> new LessonRoleDto(role.getRoleName()))
//                .toList();

        // TeacherLessonMaterialDto 생성 및 반환
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
     * 열린 질문 결과 저장
     */
    public void updateOpenQuestion(LessonOpenQuestionRequestDto lessonOpenQuestionRequestDto) {

        // lessonId에 해당하는 수업 조회
        Lesson lesson = lessonRepository.findById(lessonOpenQuestionRequestDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException(("LessonId에 맞는 수업이 없습니다.")));
    }


    /**
     * 단체 사진 저장
     */
    public void savePhoto(Long lessonId, MultipartFile photo) {

        try {
            String photoName = UUID.randomUUID().toString()+".png"; // 사진 이름 생성
            Path photoPath = Paths.get(PHOTO_DIR, photoName); // 파일 저장 경로 생성

            if (!photoPath.normalize().startsWith(Paths.get(PHOTO_DIR))) {
                throw new SecurityException("잘못된 파일 경로입니다.");
            }
            
            Files.write(photoPath, photo.getBytes()); // 파일 저장

            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("lessonId로 수업을 찾을 수 없습니다."));

            lesson.setPhotoUrl(createFileUrl(photoName)); // photoUrl 추가

            lessonRepository.save(lesson);

        } catch (IOException e) {
            throw new IllegalArgumentException("사진을 저장하지 못했습니다.");
        }
    }


    /**
     * 파일 경로를 URL 형식으로 변환
     */
    private String createFileUrl(String filePath) {
        // 파일 경로에서 중복된 루트 디렉토리를 제거
        String relativePath = filePath.replace("\\", "/");
        return "http://" + serverHost + ":" + serverPort + "/data/photo/" + relativePath;
    }
}
