package com.todock.marimo.domain.lessonmaterial.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpenQuestionRequestDto {

    @NotBlank(message = "질문 제목은 필수 입력값입니다.")
    @JsonProperty("questionTitle")
    private String questionTitle; // 질문 제목

}
