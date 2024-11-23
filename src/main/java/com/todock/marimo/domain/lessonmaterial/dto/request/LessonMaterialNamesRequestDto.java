package com.todock.marimo.domain.lessonmaterial.dto.request;

import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialNameResponseDto;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "수업 자료 리스트는 최소 1개 이상이어야 합니다.")
    private List<LessonMaterialNameResponseDto> lessonMaterials; // 수업 자료 리스트

}
