package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.result.dto.StudentResultDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // 학생 수업결과 리스트 조회
    @Query(
            "SELECT new com.todock.marimo.domain.result.dto.StudentResultDto(" +
                    "lm.bookTitle, " +
                    "l.photoUrl, " +
                    "l.createdAt" +
                    ") " +
                    "FROM Lesson l " +
                    "LEFT JOIN LessonMaterial lm ON l.lessonMaterialId = lm.lessonMaterialId " +
                    "WHERE EXISTS " +
                    "(" +
                    "SELECT p " +
                    "FROM Participant p " +
                    "WHERE p.lesson = l AND p.userId = :userId" +
                    ")"
    )
    List<StudentResultDto> findAllLessonsWithParticipants(@Param("userId") Long userId);

    // 선생님 수업결과 조회
    @Query(
            "SELECT " +
                    "l.lessonId, " +
                    "lm.bookTitle, " +
                    "GROUP_CONCAT(p.participantName), " +
                    "l.createdAt " +
                    "FROM Lesson l " +
                    "LEFT JOIN LessonMaterial lm ON l.lessonMaterialId = lm.lessonMaterialId " +
                    "LEFT JOIN Participant p ON l = p.lesson " +
                    "WHERE EXISTS " +
                    "(" +
                    "SELECT l " +
                    "FROM Lesson l " +
                    "WHERE l.createdUserId = :userId " +
                    ")" +
                    "GROUP BY l.lessonId, lm.bookTitle, l.createdAt"
    )
    List<Object[]> findAllByCreatedUserId(@Param("userId") Long userId);

}