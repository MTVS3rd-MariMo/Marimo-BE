package com.todock.marimo.domain.lessonmaterial.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpenQuestionRequestDto {

    @JsonProperty("questionTitle")
    private String questionTitle; // 질문 제목

}
