package com.todock.marimo.domain.lessonmaterial.repository;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonMaterialRepository extends JpaRepository<LessonMaterial, Long> {

    // 유저 id로 수업 자료 전체 조회
    List<LessonMaterial> findByUserId(Long userId);

}
