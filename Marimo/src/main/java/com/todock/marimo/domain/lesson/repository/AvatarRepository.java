package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    // Avatar 엔티티에서 특정 lessonId와 연결된 아바타 리스트를 조회하는 JPA
    @Query("SELECT new com.todock.marimo.domain.lesson.dto.AvatarResponseDto(" +
            "a.userId," +
            " a.avatarImg," +
            " a.animationList" +
            ") FROM Avatar a WHERE a.lesson.lessonId = :lessonId")
    List<Avatar> findAvatarByLessonId(@Param("lessonId") Long lessonId);
}