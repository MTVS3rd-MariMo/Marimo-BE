package com.todock.marimo.domain.result.repository;

import com.todock.marimo.domain.result.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {

    Result findByLessonId(Long lessonId);
}
