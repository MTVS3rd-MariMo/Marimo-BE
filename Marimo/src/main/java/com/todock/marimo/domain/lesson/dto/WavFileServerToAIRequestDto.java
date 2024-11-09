package com.todock.marimo.domain.lesson.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WavFileServerToAIRequestDto {

    @JsonProperty("lessonId")
    private Long lessonId;

    @JsonProperty("selfIntroduceId")
    private Long selfIntroduceId;

    @JsonProperty("name")
    private String Name;

    @JsonProperty("character")
    private String character;

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
