package com.todock.marimo.domain.lessonmaterial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonMaterialRequestDto {

    private Long lessonMaterialId; // 수업 자료 id

    private List<OpenQuestionRequestDto> openQuestions;  // 열린 질문 // 2개

    private List<QuizRequestDto> quizzes;               // 퀴즈 // 2개
}
