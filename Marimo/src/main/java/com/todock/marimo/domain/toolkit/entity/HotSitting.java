package com.todock.marimo.domain.toolkit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "tbl_hot_sitting")
public class HotSitting {

    @Id
    private Long hot_sitting_id;
}
