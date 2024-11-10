package com.todock.marimo.domain.result.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_open_question_result")
public class OpenQuestionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "result_id")
    private Result result; // Result와의 ManyToOne 관계 설정

    private String question; // 열린 질문

    @OneToMany(mappedBy = "openQuestionResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpenQuestionAnswerResult> answers; // 열린 질문에 대한 대답 리스트
}