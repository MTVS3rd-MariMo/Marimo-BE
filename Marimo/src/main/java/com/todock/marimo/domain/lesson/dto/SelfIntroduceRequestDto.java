package com.todock.marimo.domain.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SelfIntroduceRequestDto {

    private Long userId;

    private Long lessonId;

    private Long selfIntNum; // 자기소개 확인 키값

    private String selfIntroduce; // 기본값
}
