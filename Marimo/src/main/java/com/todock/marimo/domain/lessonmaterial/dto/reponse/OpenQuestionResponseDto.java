package com.todock.marimo.domain.lessonmaterial.dto.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionResponseDto {

    @JsonProperty("question")
    private String questionTitle; // 질문 제목

    public OpenQuestionResponseDto(String questionTitle) {
        this.questionTitle = questionTitle;
    }
}
