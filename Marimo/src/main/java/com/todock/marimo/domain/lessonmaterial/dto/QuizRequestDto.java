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

    private String question; // 문제

    private String answer; // 정답

    private String firstChoice; // 첫번째 보기
    
    private String secondChoice; // 두번째 보기
    
    private String thirdChoice; // 세번째 보기

    private String fourthChoice; // 네번째 보기

}
