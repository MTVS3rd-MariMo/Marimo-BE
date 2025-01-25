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
    private final SelfIntroduceRepository selfIntroduceRepository;


    @Autowired
    public ResultService(
            UserRepository userRepository,
            LessonRepository lessonRepository,
            AvatarRepository avatarRepository,
            LessonMaterialRepository lessonMaterialRepository, SelfIntroduceRepository selfIntroduceRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.lessonRepository = lessonRepository;
        this.lessonMaterialRepository = lessonMaterialRepository;
        this.selfIntroduceRepository = selfIntroduceRepository;
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

        log.info("수업 찾기");
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(EntityNotFoundException::new);
        if (lesson == null) {
            throw new IllegalArgumentException("lessonId로 수업을 찾을 수 없습니다.");
        }

        log.info("수업 자료 찾기");
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lesson.getLessonMaterialId()).orElseThrow(EntityNotFoundException::new);
        ;
        if (lessonMaterial == null) {
            throw new IllegalArgumentException("lessonMaterialId로 수업 자료를 찾을 수 없습니다.");
        }

        // LessonResultDto 생성 및 초기 설정
        LessonResultDto results = new LessonResultDto(
                lessonMaterial.getBookTitle(),
                lesson.getCreatedAt()
        );


        /**
         * 수업에서 조회
         */
        // 역할(아바타, 유저id)
        log.info("Role 리스트 생성");
        List<LessonRoleResultDto> avatars = avatarRepository.findAvatarsWithUsers(lesson);
        results.setRoles(avatars);

        // 핫시팅
        log.info("hotSitting 리스트 생성");
        // 1) 네이티브 쿼리 결과 조회
        List<Object[]> rawList = selfIntroduceRepository
                .findSelfIntroduceFetch(lesson.getHotSitting().getHotSittingId());

        List<HotSittingResultDto> hotSittingResults = new ArrayList<>();

        for (Object[] object : rawList) {
            String contents = (String) object[0];
            String answers = (String) object[1];

            // 여기서는 임시값이나 별도 로직을 통해 userName, character를 생성 가능
            String userName = "a";
            String character = "b";

            // 쉼표로 분리하여 List<String>으로 변환
            List<String> answerList = Arrays.asList(answers.split(","));

            HotSittingResultDto hotSittingResult = new HotSittingResultDto(
                    contents,
                    userName,
                    character,
                    answerList
            );
            hotSittingResults.add(hotSittingResult);
        }
        results.setHotSittings(hotSittingResults);

        /*List<HotSittingResultDto> hotSittingResults = lesson.getHotSitting().getSelfIntroduces()
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
                }).collect(Collectors.toList());*/


// 수업 생성 시간
        log.info("createdAt 생성");
        results.setCreatedAt(lesson.getCreatedAt());


        /**
         * 수업자료에서 조회
         */
        // 책 제목
        log.info("bookTitle 생성");
        results.setBookTitle(lessonMaterial.getBookTitle());

        // 열린 질문
        log.info("openQuestion 리스트 생성");

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
