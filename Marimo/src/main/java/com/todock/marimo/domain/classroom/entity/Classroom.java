package com.todock.marimo.domain.classroom.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="tbl_classroom")
public class Classroom {

    @Id
    private Long classroom_id;
}
