package com.todock.marimo.domain.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Library {

    @Id
    private Long library_id;
}
