package com.todock.marimo.domain.lessonmaterial.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenQuestionAnswerDto {

    private Long userId; // 열린 질문 대답 userId

    private String answer; // 열린 질문 대답
}
