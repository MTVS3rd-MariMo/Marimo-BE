package com.todock.marimo.domain.entity.lessonmaterial;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 
 * 동화 역할 엔티티
 * 
 */

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "role_id") // 역할 id
    private Long roleId;

    @Column(name = "lesson_role_name") // 역할 이름
    private String roleName;
}