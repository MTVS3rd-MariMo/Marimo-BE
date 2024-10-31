package com.todock.marimo.domain.lessonmaterial.entity.quiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.expression.spel.ast.QualifiedIdentifier;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"lessonMaterial", "quizList"})
@NoArgsConstructor
@Table(name = "tbl_selected_quiz")
public class SelectedQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 퀴즈 id
    private Long selectedQuizId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_material_id", nullable = false) // 연결된 수업 재료 id
    private LessonMaterial lessonMaterial;

    // 선택된 퀴즈 선택
    @OneToMany(mappedBy = "selectedQuiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizList; // 선택된 퀴즈 리스트

    // 생성자
    public SelectedQuiz(LessonMaterial lessonMaterial) {

        this.lessonMaterial = lessonMaterial;
        this.quizList = new ArrayList<>();
    }

    public void setQuizList(List<Quiz> quizList) {
        this.quizList = quizList;
        quizList.forEach(quiz -> quiz.setSelectedQuiz(this));
    }


    public void addQuiz(Quiz quiz1, Quiz quiz2) {
        quiz1.setSelectedQuiz(this);
        quiz2.setSelectedQuiz(this);

        this.quizList.add(quiz1);
        this.quizList.add(quiz2);

        validateQuizCount();
    }

    // 최대 퀴즈 개수 검증
    private void validateQuizCount() {
        if (quizList.size() > 2) {
            throw new IllegalArgumentException("퀴즈는 최대 2개까지 등록 가능합니다.");
        }
    }
}
