package com.todock.marimo.domain.entity.lessonmaterial.openquestion;

import com.todock.marimo.domain.entity.lessonmaterial.LessonMaterial;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openQuestionId; // 열린 질문 id

    // 수업 자료
    @ManyToOne
    @JoinColumn(name = "lesson_material_id", nullable = false)
    private LessonMaterial lessonMaterial;

    @OneToMany(mappedBy = "openQuestion", cascade = CascadeType.ALL) // 열린 질문 대답
    private List<OpenQuestionAnswer> openQuestionAnswerList = new ArrayList<>();

}
