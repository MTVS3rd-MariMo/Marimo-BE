package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.result.dto.LessonRoleResultDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    // lessonId와 userId로 특정 아바타 조회
    Optional<Avatar> findByLesson_LessonIdAndUserId(Long lessonId, Long userId); // Optional로 수정하여 null을 방지

    @Query
            ("SELECT new com.todock.marimo.domain.result.dto.LessonRoleResultDto( " +
                    "u.userId, " +
                    "u.name, " +
                    "a.character) " +
                    "FROM Avatar a " +
                    "JOIN User u ON u.userId = a.userId " +
                    "WHERE a.lesson = :lesson")
    List<LessonRoleResultDto> findAvatarsWithUsers(@Param("lesson") Lesson lesson);
}