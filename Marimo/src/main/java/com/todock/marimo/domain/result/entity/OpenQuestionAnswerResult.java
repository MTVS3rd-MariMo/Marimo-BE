package com.todock.marimo.domain.result.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_open_question_answer_result")
public class OpenQuestionAnswerResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openQuestionAnswerId;

    @ManyToOne
    @JoinColumn(name = "open_question_result_id")
    private OpenQuestionResult openQuestionResult;

    private Long userId;

    private String userName; // 열린 질문에 대한 대답을 한 사람

    private String answer; // 열린 질문에 대한 대답
}
