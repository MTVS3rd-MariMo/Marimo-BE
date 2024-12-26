package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.dto.ParticipantListDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.Participant;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lesson.repository.HotSittingRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.repository.ParticipantRepository;
import com.todock.marimo.domain.lessonmaterial.dto.LessonQuizDto;
import com.todock.marimo.domain.lessonmaterial.dto.ParticipantLessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.OpenQuestionForLessonResponseDto;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.entity.LessonRole;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


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
    }


    /**
     * 수업 생성 - lessonMaterialId를 받고 수업 자료와 LessonId 반환
     */
    @Transactional
    public Long createLesson(Long userId, Long lessonMaterialId) {

        Lesson newLesson = new Lesson(userId, lessonMaterialId);
        lessonRepository.save(newLesson);

        HotSitting newHotSitting = new HotSitting();
        newHotSitting.setLesson(newLesson);

        hotSittingRepository.save(newHotSitting);

        newLesson.setHotSitting(newHotSitting);
        lessonRepository.save(newLesson);

        Long lessonId = newLesson.getLessonId();

        log.info("생성된 lessonId: {}, 적용된 lessonMaterialId: {}", lessonId, newLesson.getLessonMaterialId());

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

    /**
     * 참가자들이 수업에 사용하는 수업자료를 lessonMaterialId로 요청
     */
    public ParticipantLessonMaterialDto getLessonMaterialById(Long lessonMaterialId) {

        Lesson lesson = lessonRepository.findById(lessonMaterialId).orElse(null);
        // lessonMaterial 조회
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("수업자료 not found with id: " + lessonMaterialId));

        // openQuestions 변환
        List<OpenQuestionForLessonResponseDto> openQuestions = lessonMaterial.getOpenQuestionList().stream()
                .map(openQuestion -> new OpenQuestionForLessonResponseDto(
                        openQuestion.getOpenQuestionId(),
                        openQuestion.getQuestion()))
                .toList();

        // quizzes 변환
        List<LessonQuizDto> quizzes = lessonMaterial.getQuizList().stream()
                .map(quiz -> new LessonQuizDto(
                        quiz.getQuestion(),
                        quiz.getAnswer(),
                        quiz.getChoices1(),
                        quiz.getChoices2(),
                        quiz.getChoices3(),
                        quiz.getChoices4()
                ))
                .toList();

        // lessonRoles 변환
        // lessonRoles를 List<LessonRoleDto> -> List<String>으로 변환
        List<String> lessonRoles = lessonMaterial.getLessonRoleList().stream()
                .map(LessonRole::getRoleName) // LessonRole 객체의 역할 이름을 추출
                .toList();

        // TeacherLessonMaterialDto 생성 및 반환
        return new ParticipantLessonMaterialDto(
                lessonMaterial.getBookTitle(),
                lessonMaterial.getBookContents(),
                lessonMaterial.getAuthor(),
                lessonMaterial.getBackgroundUrl(),
                quizzes,
                openQuestions,
                lessonRoles
        );
    }

}
