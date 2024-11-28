package com.todock.marimo.domain.lessonmaterial.repository;

import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

}
