package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {


    @Query("SELECT l FROM Lesson l JOIN l.participantList p WHERE p.userId = :userId")
    List<Lesson> findAllByUserId(@Param("userId") Long userId);
}
