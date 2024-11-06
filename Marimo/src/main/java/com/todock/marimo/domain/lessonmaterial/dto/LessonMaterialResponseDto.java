package com.todock.marimo.domain.lessonmaterial.dto;

import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LessonMaterialResponseDto { // pdf 결과 전송

    // 수업 자료 Id
    private Long LessonMaterialId;

    // 퀴즈 리스트
    private List<QuizDto> quizList;

    // 열린 질문 리스트
    private List<OpenQuestionResponseDto> openQuestions;

}
