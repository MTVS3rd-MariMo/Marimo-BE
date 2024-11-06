package com.todock.marimo.domain.lessonmaterial.dto;

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
public class StudentLessonMaterialDto {
    
    private String bookTitle;

    private String bookContents;

    private List<LessonQuizDto> quizzes = new ArrayList<>();

    private List<OpenQuestionResponseDto> openQuestions = new ArrayList<>();

    private List<LessonRoleDto> lessonRoles = new ArrayList<>();
}
