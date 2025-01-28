package com.todock.marimo.domain.lessonmaterial.repository;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.result.dto.LessonResultDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonMaterialRepository extends JpaRepository<LessonMaterial, Long> {

    // 유저 id로 수업 자료 전체 조회
    List<LessonMaterial> findByUserId(Long userId);

}
