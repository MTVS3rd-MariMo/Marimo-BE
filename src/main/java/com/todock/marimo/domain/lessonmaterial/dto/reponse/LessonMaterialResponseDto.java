package com.todock.marimo.domain.lessonmaterial.dto.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todock.marimo.domain.lessonmaterial.dto.QuizDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LessonMaterialResponseDto { // pdf 결과 전송

    // 수업 자료 Id
    @JsonProperty("lessonMaterialId")
    private Long lessonMaterialId;

    // 퀴즈 리스트
    @JsonProperty("quizList")
    private List<QuizDto> quizList;

    // 열린 질문 리스트
    @JsonProperty("openQuestionList")
    private List<OpenQuestionResponseDto> open_questions;

}
