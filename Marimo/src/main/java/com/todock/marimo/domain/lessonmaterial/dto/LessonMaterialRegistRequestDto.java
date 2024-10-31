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
public class LessonMaterialRegistRequestDto {

    private Long userId; // 만든 유저의 id

    private String bookTitle; // 책 제목

    private String bookContents; // 책 내용

    private List<OpenQuestionRequestDto> openQuestionList;  // 열린 질문 // 2개

    private List<QuizRequestDto> quizzeList;               // 퀴즈 // 3개

    private List<LessonRoleRequestDto> roleList;                 // 역할 // 4개

}
