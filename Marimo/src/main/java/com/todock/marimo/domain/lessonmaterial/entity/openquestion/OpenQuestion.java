package com.todock.marimo.domain.lessonmaterial.entity.openquestion;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tbl_open_question")
public class OpenQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openQuestionId; // 열린 질문 id

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_material_id", nullable = false) // 수업 자료
    private LessonMaterial lessonMaterial;

    @Column(name = "open_question", nullable = false) // 열린 질문(내용)
    private String question;

    @JsonManagedReference
    @OneToMany(mappedBy = "openQuestion", // 열린 질문 답변 
            cascade = CascadeType.ALL, // 질문 삭제시 답변도 삭제
            orphanRemoval = true) // 고아 객체 자동으로 삭제
    private List<OpenQuestionAnswer> openQuestionAnswerList;

    // 생성자
    public OpenQuestion(LessonMaterial lessonMaterial, String question) {

        this.lessonMaterial = lessonMaterial;
        this.question = question;
    }


    @Override
    public String toString() {
        return "OpenQuestion{" +
                "question='" + question + '\'' +
                ", openQuestionAnswerList=" + openQuestionAnswerList +
                '}';
    }
}
