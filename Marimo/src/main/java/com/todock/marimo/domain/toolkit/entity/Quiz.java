package com.todock.marimo.domain.toolkit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="tbl_quiz")
public class Quiz {

    @Id
    private Long quiz_id;
}
