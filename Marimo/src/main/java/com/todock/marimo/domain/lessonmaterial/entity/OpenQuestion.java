package com.todock.marimo.domain.lessonmaterial.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="tbl_open_question")
public class OpenQuestion {

    @Id
    private Long open_question_id;
}
