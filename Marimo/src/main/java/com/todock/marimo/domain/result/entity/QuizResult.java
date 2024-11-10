package com.todock.marimo.domain.result.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {

    private String question;

    private int answer;

    private String choice1;

    private String choice2;

    private String choice3;

    private String choice4;
}
