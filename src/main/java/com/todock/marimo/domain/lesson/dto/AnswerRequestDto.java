package com.todock.marimo.domain.lesson.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequestDto {

    @NotNull(message = "수업 ID는 필수 입력값입니다.")
    @JsonProperty("lessnId") // 구조체 때문에 o 제거
    private Long lessonId; // 수업 ID

    @NotNull(message = "질문 ID는 필수 입력값입니다.")
    private Long questionId; // 질문 ID

    @NotBlank(message = "답변은 필수 입력값입니다.")
    private String answer; // 답변 내용
}
