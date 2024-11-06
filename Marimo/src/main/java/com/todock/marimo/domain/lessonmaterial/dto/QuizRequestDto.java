package com.todock.marimo.domain.lessonmaterial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequestDto {

    private Long quizId;       // 퀴즈 ID
    private String question; // 문제

    private int answer; // 정답

    private String choices1; // 첫번째 보기
    
    private String choices2; // 두번째 보기
    
    private String choices3; // 세번째 보기

    private String choices4; // 네번째 보기

}
