package com.todock.marimo.domain.classroom.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Classroom {

    @Id
    private Long classroom_id;
}
