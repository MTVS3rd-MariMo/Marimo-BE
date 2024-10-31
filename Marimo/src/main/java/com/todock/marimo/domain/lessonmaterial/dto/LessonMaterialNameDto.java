package com.todock.marimo.domain.lessonmaterial.dto;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LessonMaterialNameDto {
    private Long lessonMaterialId;
    private String bookTitle;
    private String lessonMaterialContents;

    public LessonMaterialNameDto(LessonMaterial lessonMaterial) {
        this.lessonMaterialId = lessonMaterial.getLessonMaterialId();
        this.bookTitle = lessonMaterial.getBookTitle();
    }
}