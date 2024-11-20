package com.todock.marimo.domain.lessonmaterial.dto;

import com.todock.marimo.domain.lessonmaterial.dto.reponse.OpenQuestionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 학생용 수업 자료 호출
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherLessonMaterialDto {

    private Long LessonId;

    private Long LessonMaterialId;

    private String bookTitle;

    private String bookContents;

    private List<OpenQuestionResponseDto> openQuestions = new ArrayList<>();

    private List<QuizDto> quizzes = new ArrayList<>();

    private List<LessonRoleDto> lessonRoles = new ArrayList<>();
}
