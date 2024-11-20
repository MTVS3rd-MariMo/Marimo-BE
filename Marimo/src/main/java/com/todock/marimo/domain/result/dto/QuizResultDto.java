package com.todock.marimo.domain.result.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDto {

    private String question;

    private int answer;

    private String choices1;

    private String choices2;

    private String choices3;

    private String choices4;

}
