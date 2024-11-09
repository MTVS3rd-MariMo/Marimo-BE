package com.todock.marimo.domain.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HotSittingAIRequestDto {

    private Long lessonId;       // JSON에서 lessonId를 받기 위한 필드
    private Long selfIntroduceId; // JSON에서 selfIntroduceId를 받기 위한 필드
    private List<String> contents = new ArrayList<>(); // JSON의 contents 배열을 받기 위한 필드

    public HotSittingAIRequestDto(Long lessonId, Long selfIntroduceId) {

        this.lessonId = lessonId;
        this.selfIntroduceId = selfIntroduceId;

    }

    @Override
    public String toString() {
        return "HotSittingAIRequestDto{" +
                "lessonId=" + lessonId +
                ", selfIntroduceId=" + selfIntroduceId +
                ", contents=" + contents +
                '}';
    }
}
