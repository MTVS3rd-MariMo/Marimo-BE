package com.todock.marimo.domain.result.service;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.Participant;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.entity.hotsitting.QuestionAnswer;
import com.todock.marimo.domain.lesson.repository.AvatarRepository;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.repository.ParticipantRepository;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.result.dto.*;
import com.todock.marimo.domain.user.entity.User;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResultService {

    private final LessonRepository lessonRepository;
    private final ParticipantRepository participantRepository;
    private final LessonMaterialRepository lessonMaterialRepository;
    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;


    @Autowired
    public ResultService(
            UserRepository userRepository,
            LessonRepository lessonRepository,
            AvatarRepository avatarRepository,
            ParticipantRepository participantRepository,
            LessonMaterialRepository lessonMaterialRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.lessonRepository = lessonRepository;
        this.participantRepository = participantRepository;
        this.lessonMaterialRepository = lessonMaterialRepository;
    }


    /**
     * 학생이 참가한 모든 수업 리스트 조회 (사진 리스트로 보여줌) - LessonId, photoList 반환
     */
    public StudentResultResponseDto findAllPhotos(Long userId) {

        List<Lesson> lessons = lessonRepository.findAllLessonsWithParticipants(userId);

        log.info("Lessons fetched: {}", lessons.size());
        log.info("userId : {}", userId);

        List<StudentResultDto> photos = lessons
                .stream()
                .map(lesson -> {
                    // lessonMaterialId를 통해 해당 수업 자료의 책 제목을 조회

                    String bookTitle = lessonMaterialRepository.findByLessonMaterialId(
                                    lesson.getLessonMaterialId())
                            //.orElseThrow(() ->
                            //        new EntityNotFoundException("lessonMaterialId에 맞는 수업 자료가 없습니다."))
                            .getBookTitle();

                    return new StudentResultDto(
                            bookTitle,             // 수업 자료의 책 제목
                            lesson.getPhotoUrl(),  // 단체사진
                            lesson.getCreatedAt()  // 수업 날짜
                    );
                })
                .collect(Collectors.toList());
        // 최신순 정렬
        Collections.reverse(photos);

        return new StudentResultResponseDto(photos);
    }


    /**
     * 선생님이 참가한 모든 수업 조회 (lessonId, 책 제목, 참가자 리스트, 생성 날짜)
     */
    public TeacherResultResponseDto findAllLessons(Long userId) {

        List<TeacherResultDto> results = lessonRepository.findAllByCreatedUserId(userId)
                .stream()
                .map(lesson -> new TeacherResultDto(
                        lesson.getLessonId(),
                        lessonMaterialRepository
                                .findById(lesson.getLessonMaterialId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "lesson.getLessonId : " + lesson.getLessonId()
                                                + "의 lessonMaterialId로 수업 자료를 찾을 수 없습니다."))
                                .getBookTitle(),
                        lesson.getParticipantList()
                                .stream()
                                .map(Participant::getParticipantName)
                                .collect(Collectors.toList()),
                        lesson.getCreatedAt() != null
                                ? lesson.getCreatedAt() // 포맷팅 없이 문자열 반환
                                : "생성일시 없음" // 기본값 또는 null인 경우 처리
                ))
                .collect(Collectors.toList());

        Collections.reverse(results);

        return new TeacherResultResponseDto(results);
    }


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
                lesson.getCreatedAt()
        );

        // 열린 질문 설정
        List<OpenQuestionResultDto> openQuestions = lessonMaterial.getOpenQuestionList().stream()
                .map(question -> new OpenQuestionResultDto(
                        question.getQuestion(),
                        question.getOpenQuestionAnswerList().stream()
                                // lessonId로 필터링
                                .filter(answer -> answer.getLessonId().equals(lessonId))
                                .map(answer -> {
                                    // 유저 정보를 가져와 DTO 생성
                                    User user = userRepository.findById(answer.getUserId())
                                            .orElseThrow(() -> new IllegalArgumentException("userId로 유저를 찾을 수 없습니다."));
                                    return new ResultAnswerDto(
                                            user.getName(),
                                            answer.getAnswer());
                                })
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

        lessonResultDto.setOpenQuestions(openQuestions); // 열린 질문 리스트 설정

        // 핫시팅 설정
        List<HotSittingResultDto> hotSittings = lesson.getHotSitting().getSelfIntroduces().stream()
                .map(selfIntroduce -> new HotSittingResultDto(
                        selfIntroduce.getContents(),
                        userRepository.findById(selfIntroduce.getUserId()).orElseThrow(() ->
                                new EntityNotFoundException("userId로 유저이름을 찾을 수 없습니다.")).getName(),
                        avatarRepository.findByLesson_LessonIdAndUserId(
                                lessonId, selfIntroduce.getUserId()).orElseThrow(() ->
                                new EntityNotFoundException("lessonId와 userId로 역할을 찾을 수 없습니다.")).getCharacter(),
                        selfIntroduce.getQuestionAnswers().stream()
                                .map(QuestionAnswer::getQnAContents)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        lessonResultDto.setHotSittings(hotSittings); // 핫시팅 리스트 설정

        // 역할 설정과 아바타 이미지 설정을 통합
        List<LessonRoleResultDto> roles = lesson.getAvatarList().stream()
                .map(role -> {
                    // 해당 역할에 매칭되는 아바타 찾기
                    Avatar avatar = lesson.getAvatarList().stream()
                            .filter(a -> a.getCharacter() != null && a.getCharacter().equals(role.getCharacter()))
                            .findFirst()
                            .orElse(null);

                    String avatarUrl = (avatar != null) ? avatar.getAvatarImg() : "defaultAvatarUrl";
                    String userName = (avatar != null) ? userRepository.findById(avatar.getUserId())
                            .map(User::getName)
                            .orElse("존재하지 않는 유저입니다.") : "Unknown User";

                    return new LessonRoleResultDto(
                            userName, // 유저 이름
                            role.getCharacter() // 역할 이름
                    );
                })
                .collect(Collectors.toList());
        lessonResultDto.setRoles(roles); // 역할 리스트 설정

        return lessonResultDto;
    }

}
