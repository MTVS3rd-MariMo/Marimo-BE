package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

}
