package com.todock.marimo.domain.lessonmaterial.dto;

import com.todock.marimo.domain.lessonmaterial.entity.quiz.SelectedQuiz;
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

    private Long LessonMaterialId; // 수업 자료 id

    private List<OpenQuestionDto> openQuestions;  // 열린 질문 // 2개

    private List<QuizDto> quizzes;               // 퀴즈 // 2개
}
