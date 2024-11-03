package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    // Avatar 엔티티에서 특정 lessonId와 연결된 아바타 리스트를 조회하는 JPA
    List<Avatar> findByLesson_LessonId(Long lessonId);
}