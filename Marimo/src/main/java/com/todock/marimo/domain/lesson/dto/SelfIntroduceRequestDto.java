package com.todock.marimo.domain.lesson.dto;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SelfIntroduceRequestDto {

    private Long lessonId;

    private Long selfIntNum; // 자기소개 확인 키값

    private String selfIntroduce; // 기본값
}
