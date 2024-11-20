package com.todock.marimo.domain.lessonmaterial.dto.request;

import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialNameResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonMaterialNamesRequestDto {

    private List<LessonMaterialNameResponseDto> lessonMaterials; // 수업 자료 리스트

}
