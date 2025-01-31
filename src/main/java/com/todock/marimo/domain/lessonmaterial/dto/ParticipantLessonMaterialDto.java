package com.todock.marimo.domain.lessonmaterial.dto;

import com.todock.marimo.domain.lessonmaterial.dto.reponse.OpenQuestionForLessonResponseDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 참가자 수업 자료 호출
 */

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantLessonMaterialDto {


    private String bookTitle;

    private String bookContents;

    private String author;

    private String backgroundUrl;

    private List<LessonQuizDto> quizzes = new ArrayList<>();

    private List<OpenQuestionForLessonResponseDto> openQuestions = new ArrayList<>();

    private List<String> lessonRoles = new ArrayList<>();
}
