package com.todock.marimo.domain.lesson.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequestDto {

    @JsonProperty("lessnId") // 구조체때문에 o뺌
    private Long lessonId;

    private Long questionId;

    private String answer;
}
