package com.todock.marimo.domain.lesson.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="tbl_classroom")
public class Lesson {

    @Id
    private Long classroom_id;
}
