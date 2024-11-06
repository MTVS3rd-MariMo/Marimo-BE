package com.todock.marimo.domain.lessonresult.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelfIntroduceRequestDto {

    private Long selfIntroduceId; // 음성과 식별하기 위한 Id

    private Long LessonId; // 수업 id

    private String selfIntroduce; // 자기소개

}
