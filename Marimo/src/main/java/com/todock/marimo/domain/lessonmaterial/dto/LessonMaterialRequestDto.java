package com.todock.marimo.domain.lessonmaterial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonMaterialRequestDto {

    private Long userId; // 만든 유저의 id

    private List<OpenQuestionDto> openQuestionList;  // 열린 질문 // 2개

    private List<QuizDto> quizzeList;               // 퀴즈 // 2개
}
