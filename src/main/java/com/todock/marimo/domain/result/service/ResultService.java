package com.todock.marimo.domain.result.service;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.Participant;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lesson.entity.hotsitting.QuestionAnswer;
import com.todock.marimo.domain.lesson.repository.AvatarRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.result.dto.*;
import com.todock.marimo.domain.user.entity.User;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResultService {

    private final LessonRepository lessonRepository;
    private final LessonMaterialRepository lessonMaterialRepository;
    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;


    @Autowired
    public ResultService(
            UserRepository userRepository,
            LessonRepository lessonRepository,
            AvatarRepository avatarRepository,
            LessonMaterialRepository lessonMaterialRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.lessonRepository = lessonRepository;
        this.lessonMaterialRepository = lessonMaterialRepository;
    }


    /**
     * 학생이 참가한 모든 수업 리스트 조회 (사진 리스트로 보여줌) - LessonId, photoList 반환
     */
    public StudentResultResponseDto findAllPhotos(Long userId) {

        List<StudentResultDto> results = lessonRepository.findAllLessonsWithParticipants(userId);

        Collections.reverse(results);

        return new StudentResultResponseDto(results);
    }


    /**
     * 선생님이 참가한 모든 수업 조회 (lessonId, 책 제목, 참가자 리스트, 생성 날짜)
     */
    public TeacherResultResponseDto findAllLessons(Long userId) {

        List<Object[]> objects = lessonRepository.findAllByCreatedUserId(userId);
        // Response DTO 변환
        List<TeacherResultDto> results = objects.stream().map(
                object -> {
                    Long lessonId = (Long) object[0];
                    String bookTitle = object[1].toString();
                    List<String> participantList = Arrays.asList(object[2].toString().split(","));
                    String createdAt = object[3].toString();

                    return new TeacherResultDto(
                            lessonId,
                            bookTitle,
                            participantList,
                            createdAt
                    );
                }
        ).collect(Collectors.toList());

        Collections.reverse(results);

        return new TeacherResultResponseDto(results);
    }


    /**
     * 선생님이 참가한 수업 상세 조회
     */
    public LessonResultDto lessonDetail(Long lessonId) {

        // lesson 조회
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("lessonId로 수업을 찾을 수 없습니다."));

        // lessonMaterialId 조회
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lesson.getLessonMaterialId())
                .orElseThrow(() -> new IllegalArgumentException("lessonMaterialId로 수업 자료를 찾을 수 없습니다."));

        // LessonResultDto 생성 및 초기 설정
        LessonResultDto results = new LessonResultDto(
                lessonMaterial.getBookTitle(),
                lesson.getCreatedAt()
        );


        /**
         * 수업에서 조회
         */
        // 역할(아바타, 유저id)
        List<LessonRoleResultDto> lessonRoleResults = lesson.getAvatarList()
                .stream()
                .map(role -> {
                    User user = userRepository.getById(role.getUserId());

                    return new LessonRoleResultDto(
                            user.getName(),
                            role.getCharacter()
                    );
                }).collect(Collectors.toList());

        results.setRoles(lessonRoleResults);

        // 핫시팅
        List<HotSittingResultDto> hotSittingResults = lesson.getHotSitting().getSelfIntroduces()
                .stream()
                .map(selfIntroduce -> {

                    String contents = selfIntroduce.getContents();
                    User user = userRepository.getById(selfIntroduce.getUserId());
                    Avatar avatar = avatarRepository.findByLesson_LessonIdAndUserId(
                                    lesson.getLessonId(), user.getUserId())
                            .orElseThrow(() -> new EntityNotFoundException("lessonId와 userId로 아바타를 찾을 수 없습니다."));
                    List<String> questionAnswers = new ArrayList<>();
                    selfIntroduce.getQuestionAnswers()
                            .stream()
                            .map(answer -> questionAnswers.add(answer.getQnAContents()))
                            .collect(Collectors.toList());

                    return new HotSittingResultDto(
                            contents,
                            user.getName(),
                            avatar.getCharacter(),
                            questionAnswers
                    );
                }).collect(Collectors.toList());

        results.setHotSittings(hotSittingResults);

        // 수업 생성 시간
        results.setCreatedAt(lesson.getCreatedAt());


        /**
         * 수업자료에서 조회
         */
        // 책 제목
        results.setBookTitle(lessonMaterial.getBookTitle());

        // 열린 질문
        List<OpenQuestionResultDto> openQuestionResults = lessonMaterial.getOpenQuestionList()
                .stream()
                .map(question -> new OpenQuestionResultDto(
                        question.getQuestion(),
                        question.getOpenQuestionAnswerList().stream()
                                .filter(answer -> answer.getLessonId().equals(lessonId)) // lessonId가 맞는거만 서칭
                                .map(answer -> {
                                    User user = userRepository.findById(answer.getUserId())
                                            .orElseThrow(()
                                                    -> new EntityNotFoundException("userId에 맞는 유저가 없습니다."));

                                    return new ResultAnswerDto(
                                            user.getName(),
                                            answer.getAnswer()
                                    );

                                }).collect(Collectors.toList()))
                ).collect(Collectors.toList());


        results.setOpenQuestions(openQuestionResults);
        return results;
    }

}
