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

        validateOpenQuestionTitle(question); // 답변 검증

        this.lessonMaterial = lessonMaterial;
        this.question = question;
    }

    // 동화 제목 검증
    private void validateOpenQuestionTitle(String questionTitle) {
        if (questionTitle == null || questionTitle.isEmpty()) {
            throw new IllegalArgumentException("질문이 없습니다.");
        }
    }

    // 질문 제목 검증
    private void validateOpenQuestionUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("유저 ID가 없습니다.");
        }
    }

    // 답변 검증
    private void validateOpenQuestionAnswer(OpenQuestionAnswer openQuestionAnswer) {
        if (openQuestionAnswer == null) {
            throw new IllegalArgumentException("답변은 null일 수 없습니다.");
        }
    }

    @Override
    public String toString() {
        return "OpenQuestion{" +
                "question='" + question + '\'' +
                ", openQuestionAnswerList=" + openQuestionAnswerList +
                '}';
    }
}
