package com.todock.marimo.domain.lessonmaterial.entity.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 퀴즈 엔티티
 */

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 퀴즈 id
    private Long quizId;

    // 선택된 퀴즈는 여러개의 퀴즈를 가진다.
    @ManyToOne
    @JoinColumn(name = "selected_quiz_id", nullable = false)
    private SelectedQuiz selectedQuiz;

    @Column(name = "question") // 퀴즈 제목
    private String question;

    @Column(name = "answer") // 퀴즈 정답
    private String answer;

    @Column(name = "first_choice") // 첫번째 보기
    private String firstChoice;

    @Column(name = "second_choice") // 두번째 보기
    private String secondChoice;

    @Column(name = "third_choice") // 세번째 보기
    private String thirdChoice;

    @Column(name = "fourth_choice")// 네번째 보기
    private String fourthChoice;
}