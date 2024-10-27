package com.todock.marimo.domain.lessonmaterial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 동화 역할 엔티티
 */

@Entity
@Getter
@ToString
@NoArgsConstructor
@Table(name = "tbl_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long RoleId;

    @ManyToOne
    @JoinColumn(name = "lesson_material_id")
    private LessonMaterial lessonMaterial;

    @Column(name = "user_id") // 선택한 유저
    private Long userId;

    @Column(name = "lesson_role_name", nullable = false) // 역할 이름
    private String roleName;

    public Role(Long userId, String roleName) {

        validateRoleName(roleName);

        this.userId = userId;
        this.roleName = roleName;
    }

    private static void validateRoleName(String roleName) {
        if (roleName == null || "".equals(roleName.trim())) { // trim : 공백만 입력 방지
            throw new IllegalArgumentException("역할의 이름이 없습니다.");
        }
    }

    // 사용자 할당 여부 확인
    public void assignUser(Long userId) {
        if (this.userId != null) {
            throw new IllegalArgumentException("이미 할당도니 역할입니다.");
        }
        this.userId = userId;
    }

    public void setLessonMaterial(LessonMaterial lessonMaterial) {
        this.lessonMaterial = lessonMaterial;
    }
}