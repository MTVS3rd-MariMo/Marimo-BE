package com.todock.marimo.domain.lessonmaterial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpenQuestionUpdateDto {

    private Long questionId;

    private String questionTitle; // 질문 제목

}
