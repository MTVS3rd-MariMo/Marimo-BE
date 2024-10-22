package com.todock.marimo.domain.photo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="tbl_photo")
public class Photo {

    @Id
    private Long photo_id;
}
