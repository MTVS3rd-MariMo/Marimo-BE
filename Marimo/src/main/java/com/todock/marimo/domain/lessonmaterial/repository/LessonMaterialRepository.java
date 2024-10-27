package com.todock.marimo.domain.lessonmaterial.repository;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonMaterialRepository extends JpaRepository<LessonMaterial, Long> {
    
    // 수업 자료 저장
    
    // 유저 id로 수업 자료 조회
    
    // 수업 자료 id로 수업 자료 수정
    
    // 수업 자료 id로 수업 자료 삭제
}
