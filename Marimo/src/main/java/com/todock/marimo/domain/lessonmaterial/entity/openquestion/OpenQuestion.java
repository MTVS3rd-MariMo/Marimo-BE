package com.todock.marimo.domain.lessonmaterial.entity.openquestion;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_open_question")
public class OpenQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long open_question_id;

    // 수업 준비는 여러개의 열린 질문을 가진다.
    @ManyToOne
    @JoinColumn(name = "lesson_material_id", nullable = false)
    private LessonMaterial lessonMaterial;

    @Column(name = "contents")
    private String contents;

}
