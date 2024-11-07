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

    @JsonProperty("quiz_id")
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

}
