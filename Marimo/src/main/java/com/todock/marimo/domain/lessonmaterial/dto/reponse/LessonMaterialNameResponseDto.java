package com.todock.marimo.domain.lessonmaterial.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonMaterialNameResponseDto {

    private Long lessonMaterialId;

    private String bookTitle;

}
