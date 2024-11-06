package com.todock.marimo.domain.lessonresult.repository;

import com.todock.marimo.domain.lessonresult.entity.LessonResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonResultRepository extends JpaRepository<LessonResult, Long> {

    LessonResult findByLessonId(Long lessonId);
}
