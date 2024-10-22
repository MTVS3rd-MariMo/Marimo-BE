package com.todock.marimo.domain.toolkit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="tbl_toolkit")
public class Toolkit {

    @Id
    private Long toolkit_id;
}
