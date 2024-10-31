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
@ToString(exclude = "selectedQuiz")
@NoArgsConstructor
@Table(name = "tbl_quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 퀴즈 id
    private Long quizId;

    // 선택된 퀴즈는 여러개의 퀴즈를 가진다.
    @ManyToOne(fetch = FetchType.LAZY)
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

    // 생성자
    public Quiz(String question, String answer,
                String firstChoice, String secondChoice,
                String thirdChoice, String fourthChoice) {

        // 검증
        validateQuizQuestion(question);
        validateQuizAnswer(answer);
        validateChoices(answer, firstChoice, secondChoice, thirdChoice, fourthChoice);  // answer를 첫 번째 인자로 전달

        this.question = question;
        this.answer = answer;
        this.firstChoice = firstChoice;
        this.secondChoice = secondChoice;
        this.thirdChoice = thirdChoice;
        this.fourthChoice = fourthChoice;
    }


    private void validateQuizQuestion(String question) {

        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("문제를 작성해야합니다.");

        }
    }

    private void validateQuizAnswer(String answer) {

        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("정답을 작성해야합니다.");

        }
    }

    private void validateChoices(String answer, String... choices) {
        boolean validateIsAnswer = false;

        for (String choice : choices) {
            if (choice == null || choice.trim().isEmpty()) {
                throw new IllegalArgumentException("보기를 작성해야 합니다.");
            }
            if (choice.equals(answer)) {  // 정확한 비교를 위해 equals 사용
                validateIsAnswer = true;
            }
        }

        if (!validateIsAnswer) {
            throw new IllegalArgumentException("정답이 보기에 없습니다.");
        }
    }

    void setSelectedQuiz(SelectedQuiz selectedQuiz) {
        this.selectedQuiz = selectedQuiz;
    }

}