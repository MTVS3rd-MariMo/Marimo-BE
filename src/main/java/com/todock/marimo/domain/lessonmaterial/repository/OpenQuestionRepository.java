package com.todock.marimo.domain.lessonmaterial.repository;

import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OpenQuestionRepository extends JpaRepository<OpenQuestion, Long> {


    @Query(
            "SELECT oq " +
                    "FROM OpenQuestion oq " +
                    "LEFT JOIN FETCH oq.openQuestionAnswerList " +
                    "WHERE oq.lessonMaterial.lessonMaterialId = :lessonMaterialId"
    )
    List<OpenQuestion> findOpenQuestionsWithAnswers(
            @Param("lessonMaterialId") Long lessonMaterialId);
}
