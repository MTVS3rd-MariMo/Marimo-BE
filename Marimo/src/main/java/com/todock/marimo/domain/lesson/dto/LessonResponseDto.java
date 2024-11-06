package com.todock.marimo.domain.lesson.dto;

import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialResponseDto;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponseDto {

    private Long LessonId;

    private LessonMaterialResponseDto lessonMaterialResponseDto;


}
