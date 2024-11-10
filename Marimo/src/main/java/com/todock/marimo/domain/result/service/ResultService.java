package com.todock.marimo.domain.result.service;

import com.todock.marimo.domain.lesson.dto.ParticipantDto;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.hotsitting.QuestionAnswer;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.repository.ParticipantRepository;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.result.dto.*;
import com.todock.marimo.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {

    private final LessonRepository lessonRepository;
    private final ParticipantRepository participantRepository;
    private final LessonMaterialRepository lessonMaterialRepository;
    private final UserRepository userRepository;

    @Autowired
    public ResultService(
            LessonRepository lessonRepository
            , ParticipantRepository participantRepository, LessonMaterialRepository lessonMaterialRepository, UserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.participantRepository = participantRepository;
        this.lessonMaterialRepository = lessonMaterialRepository;
        this.userRepository = userRepository;
    }


    /**
     * 학생이 참가한 모든 수업 리스트 조회 (사진 리스트로 보여줌) - LessonId, photoList 반환
     */
    public List<StudentResultDto> findAllPhotos(Long userId) {

        return participantRepository.findAllByUserId(userId)
                .stream()
                .map(participant -> new StudentResultDto(
                        participant.getLesson().getLessonId(),
                        participant.getLesson().getPhotoUrl()))
                .collect(Collectors.toList());
    }


    /**
     * 학생이 참가한 수업 사진 상세 조회 - photo 반환
     */
    /*
    public String findPhotoByLessonId(Long lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("lessonId로 수업을 찾을수 없습니다."));

        return lesson.getPhotoUrl();
    }
    */


    /**
     * 선생님이 참가한 모든 수업 조회
     */
    /*
    public List<TeacherResultDto> findAllLessons(Long userId) {

        participantRepository.findAllByUserId(userId);
         TeacherResultDto teacherResultDto;
       return teacherResultDto;

    }
    */


    /**
     * 선생님이 참가한 수업 상세 조회
     */
    /**
     * 선생님이 참가한 수업 상세 조회
     */
    public LessonResultDto lessonDetail(Long lessonId) {

        // lessonId로 Lesson을 조회
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("lessonId로 수업을 찾을 수 없습니다."));

        // lessonMaterialId로 LessonMaterial을 조회
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lesson.getLessonMaterialId())
                .orElseThrow(() -> new IllegalArgumentException("lessonMaterialId로 수업 자료를 찾을 수 없습니다."));

        // 반환할 LessonResultDto 생성 및 초기 설정
        LessonResultDto lessonResultDto = new LessonResultDto(
                lessonMaterial.getBookTitle(),
                lessonMaterial.getBookContents(),
                lesson.getPhotoUrl()
        );

        // 참가자 목록 설정
        List<ParticipantDto> participants = lesson.getParticipantList().stream()
                .map(participant -> new ParticipantDto(userRepository.findNameByUserId(participant.getUserId())))
                .collect(Collectors.toList());
        lessonResultDto.setParticipants(participants); // 참가자 리스트 설정

        // 역할 설정
        List<LessonRoleResultDto> roles = lessonMaterial.getLessonRoleList().stream()
                .map(role -> new LessonRoleResultDto(role.getRoleName()))
                .collect(Collectors.toList());
        lessonResultDto.setRoles(roles); // 역할 리스트 설정

        // 아바타 이미지 설정
        List<AvatarResultDto> avatars = lesson.getAvatarList().stream()
                .map(avatar -> new AvatarResultDto(avatar.getAvatarImg()))
                .collect(Collectors.toList());
        lessonResultDto.setAvatars(avatars); // 아바타 리스트 설정

        // 열린 질문 설정
        List<OpenQuestionResultDto> openQuestions = lessonMaterial.getOpenQuestionList().stream()
                .map(question -> new OpenQuestionResultDto(
                        question.getQuestion(),
                        question.getOpenQuestionAnswerList().stream()
                                .map(answer -> new ResultAnswerDto(
                                        userRepository.findNameByUserId(answer.getUserId()),
                                        answer.getAnswer()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        lessonResultDto.setOpenQuestions(openQuestions); // 열린 질문 리스트 설정

        // 핫시팅 설정
        List<HotSittingResultDto> hotSittings = lesson.getHotSitting().getSelfIntroduces().stream()
                .map(selfIntroduce -> new HotSittingResultDto(
                        selfIntroduce.getContents(),
                        selfIntroduce.getQuestionAnswers().stream()
                                .map(QuestionAnswer::getQnAContents)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        lessonResultDto.setHotSittings(hotSittings); // 핫시팅 리스트 설정

        // 퀴즈 설정
        List<QuizResultDto> quizzes = lessonMaterial.getQuizList().stream()
                .map(quiz -> new QuizResultDto(
                        quiz.getQuestion(),
                        quiz.getAnswer(),
                        quiz.getChoices1(),
                        quiz.getChoices2(),
                        quiz.getChoices3(),
                        quiz.getChoices4()))
                .collect(Collectors.toList());
        lessonResultDto.setQuizzes(quizzes); // 퀴즈 리스트 설정

        return lessonResultDto;
    }
}
