package com.todock.marimo.domain.lesson.repository;


import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenQuestionRepository extends JpaRepository<OpenQuestion, Long> {

}
