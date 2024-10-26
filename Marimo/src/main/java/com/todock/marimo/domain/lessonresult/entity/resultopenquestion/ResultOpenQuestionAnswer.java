package com.todock.marimo.domain.lessonresult.entity.resultopenquestion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_result_open_question_answer")
public class ResultOpenQuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openQuestionAnswerId; // 답변 id

    @ManyToOne
    @JoinColumn(name="result_open_question_id", nullable = false) // 열린 질문
    private ResultOpenQuestion resultOpenQuestion;

    @Column(name = "user_id") // 작성한 유저 id
    private Long userId;

    @Column(name = "answer") // 열린 질문 답변 내용
    private String answer;
}