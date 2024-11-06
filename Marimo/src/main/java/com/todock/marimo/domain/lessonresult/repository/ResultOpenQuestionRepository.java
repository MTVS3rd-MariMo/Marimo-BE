package com.todock.marimo.domain.lessonresult.repository;

import com.todock.marimo.domain.lessonresult.entity.resultopenquestion.ResultOpenQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultOpenQuestionRepository extends JpaRepository<ResultOpenQuestion, Long> {
}
