package com.todock.marimo.domain.lessonmaterial.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="tbl_lesson_material")
public class LessonMaterial {

    @Id
    private Long lesson_material_id;
}
