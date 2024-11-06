package com.todock.marimo.domain.lessonmaterial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenQuestionRequestDto {

    private Long openQuestionId;  // 열린 질문 ID
    private String questionTitle; // 질문 제목

}
