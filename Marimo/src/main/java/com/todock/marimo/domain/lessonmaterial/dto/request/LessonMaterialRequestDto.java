package com.todock.marimo.domain.lessonmaterial.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todock.marimo.domain.lessonmaterial.dto.QuizDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LessonMaterialRequestDto {

    @JsonProperty("lessonMaterialId")
    private Long lessonMaterialId; // 수업 자료 id

    @JsonProperty("quizList")
    private List<QuizDto> quizList;               // 퀴즈 // 2개

    @JsonProperty("openQuestionList")
    private List<OpenQuestionRequestDto> openQuestionList;  // 열린 질문 // 2개
}
