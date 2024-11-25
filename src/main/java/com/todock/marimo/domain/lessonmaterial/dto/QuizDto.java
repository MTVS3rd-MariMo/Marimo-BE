package com.todock.marimo.domain.lessonmaterial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {

    @NotNull(message = "퀴즈 ID는 필수 입력값입니다.")
    @JsonProperty("quizId")
    private Long quizId; // 퀴즈 ID

    @NotBlank(message = "퀴즈 문제는 필수 입력값입니다.")
    @JsonProperty("question")
    private String question; // 문제

    @Min(value = 1, message = "정답은 1 이상의 값이어야 합니다.")
    @JsonProperty("answer")
    private int answer; // 정답

    @NotBlank(message = "첫 번째 보기 내용은 필수 입력값입니다.")
    @JsonProperty("choices1")
    private String choices1; // 첫 번째 보기

    @NotBlank(message = "두 번째 보기 내용은 필수 입력값입니다.")
    @JsonProperty("choices2")
    private String choices2; // 두 번째 보기

    @NotBlank(message = "세 번째 보기 내용은 필수 입력값입니다.")
    @JsonProperty("choices3")
    private String choices3; // 세 번째 보기

    @NotBlank(message = "네 번째 보기 내용은 필수 입력값입니다.")
    @JsonProperty("choices4")
    private String choices4; // 네 번째 보기

    public QuizDto(String question, int answer, String choices1, String choices2, String choices3, String choices4) {
        this.question = question;
        this.answer = answer;
        this.choices1 = choices1;
        this.choices2 = choices2;
        this.choices3 = choices3;
        this.choices4 = choices4;
    }

}