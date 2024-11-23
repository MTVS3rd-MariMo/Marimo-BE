package com.todock.marimo.domain.lesson.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SelfIntroduceRequestDto {

    @NotNull(message = "수업 ID는 필수 입력값입니다.")
    private Long lessonId; // 수업 ID

    @NotNull(message = "자기소개 키값은 필수 입력값입니다.")
    @Min(value = 0, message = "자기소개 키값은 1 이상의 값이어야 합니다.")
    @Max(value = 5, message = "자기소개 키값은 5 이하의 값이어야 합니다.")
    private Long selfIntNum; // 자기소개 확인 키값

    @NotBlank(message = "자기소개 내용은 필수 입력값입니다.")
    private String selfIntroduce; // 자기소개 내용
}
