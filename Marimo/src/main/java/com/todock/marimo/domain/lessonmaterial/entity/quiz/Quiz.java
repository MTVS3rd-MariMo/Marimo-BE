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

    // 생성자
    public Quiz(String question, String answer,
                String firstChoice, String secondChoice,
                String thirdChoice, String fourthChoice) {

        // 검증
        validateQuizQuestion(question);
        validateQuizAnswer(answer);
        validateChoices(firstChoice, secondChoice, thirdChoice, fourthChoice);

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

        boolean validateIsAnswer = false; // 보기 중에 정답이 없는 상태

        for (String choice : choices) {

            if (choice == null || choice.trim().isEmpty()) { // null 체크와 trim()을 통한 공백 제거 후 빈 문자열 체크
                throw new IllegalArgumentException("보기를 작성해야 합니다.");
            }
            if (answer.equals(choice)) { // 정답이 있는지 검증
                validateIsAnswer = true; // 보기 == 정답일 때, true
            }
        }

        // 보기 중에 정답이 없으면 예외
        if (!validateIsAnswer) {
            throw new IllegalArgumentException("정답이 보기에 없습니다.");
        }
    }

    void setSelectedQuiz(SelectedQuiz selectedQuiz) {
        this.selectedQuiz = selectedQuiz;
    }

}