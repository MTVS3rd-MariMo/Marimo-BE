package com.todock.marimo.domain.classroom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="tbl_avatar")
public class Avatar {

    @Id
    private Long avatar_id;
}
