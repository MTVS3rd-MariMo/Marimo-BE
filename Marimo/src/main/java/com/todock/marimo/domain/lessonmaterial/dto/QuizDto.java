package com.todock.marimo.domain.lessonmaterial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {

    @JsonProperty("quizId")
    private Long quizId;

    @JsonProperty("question")
    private String question; // 문제

    @JsonProperty("answer")
    private int answer; // 정답

    @JsonProperty("choices1")
    private String choices1; // 첫번째 보기

    @JsonProperty("choices2")
    private String choices2; // 두번째 보기

    @JsonProperty("choices3")
    private String choices3; // 세번째 보기

    @JsonProperty("choices4")
    private String choices4; // 네번째 보기

    public QuizDto(String question, int answer, String choices1, String choices2, String choices3, String choices4) {
        this.question = question;
        this.answer = answer;
        this.choices1 = choices1;
        this.choices2 = choices2;
        this.choices3 = choices3;
        this.choices4 = choices4;
    }

    @Override
    public String toString() {
        return "QuizDto{" +
                "question='" + question + '\'' +
                ", answer=" + answer +
                ", choices1='" + choices1 + '\'' +
                ", choices2='" + choices2 + '\'' +
                ", choices3='" + choices3 + '\'' +
                ", choices4='" + choices4 + '\'' +
                '}';
    }
}
