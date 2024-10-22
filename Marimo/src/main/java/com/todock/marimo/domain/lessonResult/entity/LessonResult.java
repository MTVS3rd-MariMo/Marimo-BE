package com.todock.marimo.domain.lessonResult.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "tbl_lesson_result")
public class LessonResult {

    @Id
    private Long lesson_result_id;

}
