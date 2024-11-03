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
public class LessonMaterialResponseDto {

    private List<Quiz> quizList;
    private List<OpenQuestion> openQuestionList;

    // 필요한 필드만 포함하도록 구성
}
