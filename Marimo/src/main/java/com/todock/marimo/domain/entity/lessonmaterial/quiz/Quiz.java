package com.todock.marimo.domain.entity.lessonmaterial.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀴즈 엔티티
 */

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    // 선택된 퀴즈는 여러개의 퀴즈를 가진다.
    @ManyToOne
    @JoinColumn(name = "selected_quiz_id", nullable = false)
    private SelectedQuiz selectedQuiz;

    // 퀴즈 제목
    @Column(name = "question")
    private String question;

    // 퀴즈 정답
    @Column(name = "answer")
    private String answer;

    // 퀴즈는 여러개의 보기를 가진다.

    // mappedBy: 누가 주인인지 알려줌
    // cascade = CascadeType.ALL: 부모 엔티티에서 발생하는 모든 작업(생성, 수정, 삭제 등)을 자식 엔티티에게 전파함
    // orphanRemoval = true: 부모 엔티티에서 자식 엔티티가 더 이상 참족되지 않으면, 해당 자식 엔티티를 자동으로 삭제하는 역할
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true) // choice와 일대다
    private List<Choice> choices = new ArrayList<>();

}