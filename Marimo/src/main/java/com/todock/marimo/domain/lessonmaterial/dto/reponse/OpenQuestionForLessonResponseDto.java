package com.todock.marimo.domain.lessonmaterial.dto.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionForLessonResponseDto {

    @JsonProperty("questionId")
    private Long questionId;

    @JsonProperty("questionTitle")
    private String question; // 질문 제목

    public OpenQuestionForLessonResponseDto(Long questionId, String question) {
        this.questionId = questionId;
        this.question = question;
    }


}
