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

import java.util.ArrayList;
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
    private List<ParticipantResultDto> participants = new ArrayList<>();

    // 역할
    private List<LessonRoleResultDto> Roles = new ArrayList<>();

    // 열린 질문
    private List<OpenQuestionResultDto> openQuestions = new ArrayList<>();

    // 핫시팅
    private List<HotSittingResultDto> hotSittings = new ArrayList<>();

    // 퀴즈
    private List<QuizResultDto> quizzes = new ArrayList<>();

    // 사진
    private String photoUrl;

    public LessonResultDto(String bookTitle, String bookContents, String photoUrl) {
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
        this.photoUrl = photoUrl;
    }
}
