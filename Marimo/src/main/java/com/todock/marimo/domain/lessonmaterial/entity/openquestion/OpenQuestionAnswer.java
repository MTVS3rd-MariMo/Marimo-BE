package com.todock.marimo.domain.lessonmaterial.entity.openquestion;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_open_question_answer")
public class OpenQuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openQuestionAnswerId; // 열린 질문 대답 id

    @ManyToOne // 열린 질문
    @JoinColumn(name="open_question_id", nullable = false)
    private OpenQuestion openQuestion;

    @Column(name = "user_id", nullable = false) // 작성한 유저 id
    private Long userId;

    @Column(name = "answer") // 답변
    private String answer;

}