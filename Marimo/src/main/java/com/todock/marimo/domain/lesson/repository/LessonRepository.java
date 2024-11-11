package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findAllByParticipantListUserId(Long userId);
}
