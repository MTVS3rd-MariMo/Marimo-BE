package com.todock.marimo.domain.lessonmaterial.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("lessonMaterialId")
    private Long lessonMaterialId; // 수업 자료 id

    @JsonProperty("openQuestionList")
    private List<OpenQuestionRequestDto> openQuestionRequestList;  // 열린 질문 // 2개

    @JsonProperty("quizList")
    private List<QuizRequestDto> quizList;               // 퀴즈 // 2개
}
