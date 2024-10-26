package com.todock.marimo.domain.lessonmaterial.entity.openquestion;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_open_question")
public class OpenQuestion {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openQuestionId; // 열린 질문 id

    @ManyToOne
    @JoinColumn(name = "lesson_material_id", nullable = false) // 수업 자료
    private LessonMaterial lessonMaterial;

    @Column(name = "open_question_title") // 열린 질문 제목
    private String questionTitle;

    @OneToMany(mappedBy = "openQuestion") // 열린 질문 대답
    private List<OpenQuestionAnswer> openQuestionAnswerList = new ArrayList<>();

}
