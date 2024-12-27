package com.todock.marimo.domain.lesson.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WavFileServerToAIRequestDto {

    @NotNull(message = "lessonId는 필수 입력값입니다.")
    @JsonProperty("lessonId")
    private Long lessonId;

    @NotNull(message = "selfIntroduceId는 필수 입력값입니다.")
    @JsonProperty("selfIntroduceId")
    private Long selfIntroduceId;

    @NotNull(message = "name는 필수 입력값입니다.")
    @JsonProperty("name")
    private String Name;

    @NotNull(message = "character는 필수 입력값입니다.")
    @JsonProperty("character")
    private String character;

    @NotNull(message = "wavFile은 필수 입력값입니다.")
    @JsonProperty("wavFile")
    private String wavFile;

    @Override
    public String toString() {
        return "WavFileServerToAIRequestDto{" +
                "lessonId=" + lessonId +
                ", selfIntroduceId=" + selfIntroduceId +
                ", Name='" + Name + '\'' +
                ", character='" + character + '\'' +
                ", wavFile=" + wavFile +
                '}';
    }
}
