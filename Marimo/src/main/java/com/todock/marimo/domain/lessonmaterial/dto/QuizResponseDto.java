package com.todock.marimo.domain.lessonmaterial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDto {

    private String question;
    private int answer;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
}
