package com.todock.marimo.domain.lessonmaterial.repository;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonMaterialRepository extends JpaRepository<LessonMaterial, Long> {


    // 수업 자료 저장


    // 유저 id로 수업 자료 전체 조회
    List<LessonMaterial> findByUserId(Long userId);

    // 수업자료 id로 수업 Id 조회
    // LessonMaterial findLessonMaterialByLessonMaterialId(Long lessonMaterialId);

    // userId로 전체 조회
    //List<LessonMaterialNameResponseDto> findLessonMaterialNameByLessonMaterialId(Long userId);
}
