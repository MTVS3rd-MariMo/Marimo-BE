package com.todock.marimo.domain.lessonmaterial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * 동화 역할 엔티티
 * 
 */

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long RoleId;
    @ManyToOne
    @JoinColumn(name = "lesson_material_id")
    private LessonMaterial lessonMaterial;

    @Column(name = "lesson_role_name") // 역할 이름
    private String roleName;
}