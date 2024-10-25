package com.todock.marimo.domain.entity.lessonmaterial.openquestion;


import com.todock.marimo.domain.entity.user.User;
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

    @Column(name = "user_id", nullable = false) // 유저 id (다른 애그리거트라서 연관관계를 연결하지 않는다.)
    private Long userId;

    @Column(name = "answer")
    private String answer;

}