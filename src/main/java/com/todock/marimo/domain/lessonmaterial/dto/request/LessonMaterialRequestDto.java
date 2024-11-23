package com.todock.marimo.domain.lessonmaterial.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todock.marimo.domain.lessonmaterial.dto.QuizDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LessonMaterialRequestDto {

    @NotNull(message = "수업 자료 ID는 필수 입력값입니다.")
    @JsonProperty("lessonMaterialId")
    private Long lessonMaterialId; // 수업 자료 id

    @NotEmpty(message = "퀴즈 리스트는 최소 1개 이상이어야 합니다.")
    @JsonProperty("quizList")
    private List<QuizDto> quizList; // 퀴즈 // 2개

    @NotEmpty(message = "열린 질문 리스트는 최소 1개 이상이어야 합니다.")
    @JsonProperty("openQuestionList")
    private List<OpenQuestionRequestDto> openQuestionList;  // 열린 질문 // 2개

}
