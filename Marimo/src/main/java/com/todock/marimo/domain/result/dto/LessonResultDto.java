package com.todock.marimo.domain.result.dto;

import com.todock.marimo.domain.lesson.dto.ParticipantDto;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.entity.LessonRole;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LessonResultDto {

    // 책 제목
    private String bookTitle;

    // 책 내용
    private String bookContents;

    // 참가자
    private List<ParticipantDto> participants;

    // 역할
    private List<LessonRoleResultDto> Roles;

    // 아바타 이미지
    private List<AvatarResultDto> avatars;

    // 열린 질문
    private List<OpenQuestionResultDto> openQuestions;

    // 핫시팅
    private List<HotSittingResultDto> hotSittings;

    // 퀴즈
    private List<QuizResultDto> quizzes;

    // 사진
    private String photoUrl;

}
