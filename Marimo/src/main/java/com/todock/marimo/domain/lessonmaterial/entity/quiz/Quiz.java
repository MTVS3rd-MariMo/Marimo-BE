package com.todock.marimo.domain.lessonmaterial.entity.quiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@NoArgsConstructor
@Table(name = "tbl_quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 퀴즈 id
    private Long quizId;

    // 선택된 퀴즈는 여러개의 퀴즈를 가진다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_quiz_id", nullable = false)
    @JsonBackReference
    private SelectedQuiz selectedQuiz;

    @Column(name = "question") // 퀴즈 제목
    private String question;

    @Column(name = "answer") // 퀴즈 정답
    private int answer;

    @Column(name = "choices1") // 첫번째 보기
    private String choices1;

    @Column(name = "choices2") // 두번째 보기
    private String choices2;

    @Column(name = "choices3") // 세번째 보기
    private String choices3;

    @Column(name = "choices4")// 네번째 보기
    private String choices4;

    // 생성자
    public Quiz(String question, int answer,
                String choice1, String choice2,
                String choice3, String choice4) {

        this.question = question;
        this.answer = answer;
        this.choices1 = choice1;
        this.choices2 = choice2;
        this.choices3 = choice3;
        this.choices4 = choice4;
    }

    void setSelectedQuiz(SelectedQuiz selectedQuiz) {
        this.selectedQuiz = selectedQuiz;
    }


    @Override
    public String toString() {
        return "Quiz{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", choices1='" + choices1 + '\'' +
                ", choices2='" + choices2 + '\'' +
                ", choices3='" + choices3 + '\'' +
                ", choices4='" + choices4 + '\'' +
                '}';
    }
}