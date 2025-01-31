package com.todock.marimo.domain.result.service;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.Participant;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lesson.entity.hotsitting.QuestionAnswer;
import com.todock.marimo.domain.lesson.repository.AvatarRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.repository.SelfIntroduceRepository;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.lessonmaterial.repository.OpenQuestionRepository;
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

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final AvatarRepository avatarRepository;
    private final OpenQuestionRepository openQuestionRepository;
    private final SelfIntroduceRepository selfIntroduceRepository;
    private final LessonMaterialRepository lessonMaterialRepository;


    @Autowired
    public ResultService(
            UserRepository userRepository,
            LessonRepository lessonRepository,
            AvatarRepository avatarRepository,
            OpenQuestionRepository openQuestionRepository,
            SelfIntroduceRepository selfIntroduceRepository,
            LessonMaterialRepository lessonMaterialRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.lessonRepository = lessonRepository;
        this.openQuestionRepository = openQuestionRepository;
        this.selfIntroduceRepository = selfIntroduceRepository;
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

        log.info("수업 찾기");
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("수업을 찾을 수 없습니다."));

        log.info("수업 자료 찾기");
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lesson.getLessonMaterialId())
                .orElseThrow(() -> new EntityNotFoundException("수업자료를 찾을 수 없습니다."));

        Long lessonMaterialId = lessonMaterial.getLessonMaterialId();

        if (lessonMaterial == null) {
            throw new IllegalArgumentException("lessonMaterialId로 수업 자료를 찾을 수 없습니다.");
        }

        // 역할(아바타, 유저id)
        log.info("Role 리스트 생성");
        List<LessonRoleResultDto> avatars = avatarRepository.findAvatarsWithUsers(lesson);

        // 핫시팅
        log.info("hotSitting 리스트 생성");
        List<Object[]> selfIntroduceObjects = selfIntroduceRepository
                .findSelfIntroduceFetch(lesson.getHotSitting().getHotSittingId());

        List<HotSittingResultDto> hotSittingResults = new ArrayList<>();
        for (Object[] object : selfIntroduceObjects) {

            String contents = object[0].toString();
            Long userId = (Long) object[1];
            String answers = object[2].toString();

            LessonRoleResultDto matchingAvatar = avatars.stream()
                    .filter(avatar -> avatar.getUserId().equals(userId)) // userId와 매칭
                    .findFirst()
                    .orElse(new LessonRoleResultDto(userId, "Unknown", "Unknown")); // 기본값 설정

            String userName = matchingAvatar.getUserName();
            String character = matchingAvatar.getCharacter();

            // 쉼표로 분리하여 List<String>으로 변환
            List<String> answerList = Arrays.asList(answers.split("-"));

            HotSittingResultDto hotSittingResult = new HotSittingResultDto(
                    contents,
                    userName,
                    character,
                    answerList
            );
            hotSittingResults.add(hotSittingResult);
        }

        // 열린 질문
        log.info("openQuestion 리스트 생성");

        List<OpenQuestionResultDto> openQuestionResults = openQuestionRepository
                .findOpenQuestionsWithAnswers(lessonMaterialId)
                .stream()
                .map(openQuestion -> new OpenQuestionResultDto(
                        openQuestion.getQuestion(),

                        openQuestion.getOpenQuestionAnswerList().stream()
                                .filter(qna -> qna.getLessonId().equals(lessonId))
                                .map(qna -> new ResultAnswerDto(
                                        userRepository.findById(qna.getUserId())
                                                .orElseThrow(() -> new EntityNotFoundException("유저가 없습니다."))
                                                .getName(), // 이미 Fetch Join으로 로드된 User 엔티티 사용
                                        qna.getAnswer()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        LessonResultDto lessonResults = new LessonResultDto(
                lessonMaterial.getBookTitle(),
                lesson.getCreatedAt(),
                avatars,
                openQuestionResults,
                hotSittingResults
        );

        return lessonResults;
    }

}
