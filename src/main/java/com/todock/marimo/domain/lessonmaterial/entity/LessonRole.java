package com.todock.marimo.domain.lessonmaterial.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 동화 역할 엔티티
 */

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tbl_lesson_role")
public class LessonRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_material_id")
    private LessonMaterial lessonMaterial;

    @Setter
    @Column(name = "lesson_role_name", nullable = false) // 역할 이름
    private String roleName;

    public LessonRole(LessonMaterial lessonMaterial, String roleName) {

        this.lessonMaterial = lessonMaterial;
        this.roleName = roleName;
    }

}