package com.todock.marimo.domain.lesson.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WavFileClientToServerRequestDto {

    private Long lessonId;

    private Long selfIntNum;

    private String name;

    private String character;

    private Long selfIntroductionId; // 기본값

    private String wavFile;


    @Override
    public String toString() {
        return "WavFileToAIRequestDto{" +
                "lessonId=" + lessonId +
                ", name='" + name + '\'' +
                ", character='" + character + '\'' +
                ", selfIntNum=" + selfIntNum +
                ", wavFile=" + (wavFile != null ? "Encoded File" : "null") +
                '}';
    }
}
