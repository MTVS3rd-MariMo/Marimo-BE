package com.todock.marimo.domain.lessonresult.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ResultQuiz {

    @Column(name = "quiz_id")
    private Long quizId; // 퀴즈 id

    @Column(name = "quiz_answer")
    private Long quizAnswer; // 퀴즈 정답

    @Column(name = "first_choice") // 첫번째 보기
    private String firstChoice;

    @Column(name = "second_choice") // 두번째 보기
    private String secondChoice;

    @Column(name = "third_choice") // 세번째 보기
    private String thirdChoice;

    @Column(name = "fourth_choice")// 네번째 보기
    private String fourthChoice;

}
