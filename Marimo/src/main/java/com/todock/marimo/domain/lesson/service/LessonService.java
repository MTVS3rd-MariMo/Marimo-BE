package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.dto.LessonOpenQuestionRequestDto;
import com.todock.marimo.domain.lesson.dto.ParticipantListDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.Participant;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lessonmaterial.dto.*;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.lessonmaterial.repository.ParticipantRepository;
import com.todock.marimo.domain.lessonresult.entity.LessonResult;
import com.todock.marimo.domain.lessonresult.repository.LessonResultRepository;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class LessonService {

    private final LessonMaterialRepository lessonMaterialRepository;
    private final LessonResultRepository lessonResultRepository;
    private final ParticipantRepository participantRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Autowired
    public LessonService(LessonMaterialRepository lessonMaterialRepository
            , LessonResultRepository lessonResultRepository
            , ParticipantRepository participantRepository
            , LessonRepository lessonRepository
            , UserRepository userRepository) {
        this.lessonMaterialRepository = lessonMaterialRepository;
        this.lessonResultRepository = lessonResultRepository;
        this.participantRepository = participantRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }


    /**
     * 수업 생성 - lessonMaterialId를 받고 수업자료와 LessonId와 LessonMaterial 반환
     */
    public TeacherLessonMaterialDto createLesson(Long userId, Long lessonMaterialId) {

        // lessonMaterial 조회
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("LessonMaterial not found with id: " + lessonMaterialId));

        // Lesson 생성 및 설정
        Lesson newlesson = new Lesson();
        Long lessonId = newlesson.getLessonId(); // lessonId 추출

        // 선생님 참가자 목록에 추가하기
        updateUserByLessonId(userId, lessonId);

        // 결과를 저장할 lessonResult 객체 생성
        LessonResult lessonResult = new LessonResult();
        lessonResult.setLessonId(lessonId); // lessonId로 lessonResult 와 연결

        // openQuestions 변환
        List<OpenQuestionResponseDto> openQuestions = lessonMaterial.getOpenQuestionList().stream()
                .map(openQuestion -> new OpenQuestionResponseDto(openQuestion.getQuestion()))
                .toList();

        // quizzes 변환
        List<QuizDto> quizzes = lessonMaterial.getQuizList().stream()
                .map(quiz -> new QuizDto(
                        quiz.getQuizId(),
                        quiz.getQuestion(),
                        quiz.getAnswer(),
                        quiz.getChoices1(),
                        quiz.getChoices2(),
                        quiz.getChoices3(),
                        quiz.getChoices4()
                ))
                .toList();

        // lessonRoles 변환
        List<LessonRoleDto> lessonRoles = lessonMaterial.getLessonRoleList().stream()
                .map(role -> new LessonRoleDto(role.getRoleName()))
                .toList();

        // TeacherLessonMaterialDto 생성 및 반환
        return new TeacherLessonMaterialDto(
                newlesson.getLessonId(),
                lessonMaterial.getLessonMaterialId(),
                lessonMaterial.getBookTitle(),
                lessonMaterial.getBookContents(),
                openQuestions,
                quizzes,
                lessonRoles
        );

    }


    /**
     * LessonId로 참가자 목록에 유저Id, 유저 이름 추가하기
     */
    public void updateUserByLessonId(Long userId, Long lessonId) {

        // 수업 찾기
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다."));

        // 참가자 생성
        Participant participant = new Participant();
        participant.setUserId(userId);
        String participantName = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId))
                .getName();
        participant.setParticipantName(participantName);
        participant.setLesson(lesson);

        // 참가자 저장
        participantRepository.save(participant);

    }


    /**
     * 참가자들이 participant 목록 서버에 요청
     */
    public ParticipantListDto findParticipantByLessonId(Long lessonId) {

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
     * 열린질문 결과 저장
     */
    public void updateOpenQuestion(LessonOpenQuestionRequestDto lessonOpenQuestionRequestDto) {

        Lesson lesson = lessonRepository.findById(lessonOpenQuestionRequestDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException(("LessonId에 맞는 수업이 없습니다.")));

        // lessonResult에 열린 질문 저장
        LessonResult lessonResult = lessonResultRepository
                .findByLessonId(lessonOpenQuestionRequestDto.getLessonId());
        
        
    }
}