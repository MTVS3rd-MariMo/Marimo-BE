package com.todock.marimo.domain.lesson.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_photo")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    // 수업은 하나의 단체사진을 가진다.
    @OneToOne
    private Lesson lesson;

    @Column(name = "photo_url")
    private String photoUrl;
}