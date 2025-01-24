package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    // lessonId와 userId로 특정 아바타 조회
    Optional<Avatar> findByLesson_LessonIdAndUserId(Long lessonId,Long userId); // Optional로 수정하여 null을 방지

    Long userId(Long userId);
}