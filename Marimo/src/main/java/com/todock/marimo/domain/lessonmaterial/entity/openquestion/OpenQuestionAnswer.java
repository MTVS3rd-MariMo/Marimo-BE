package com.todock.marimo.domain.lessonmaterial.entity.openquestion;


import com.todock.marimo.domain.user.entity.User;
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
    private Long open_question_answer_id;

    // 유저는 여러개의 답변을 가진다.
    @ManyToOne
    @JoinColumn(name="open_question_id")
    private OpenQuestion open_question;

    // 유저는 여러개의 답변을 가진다.
    @ManyToOne
    private User user;

    @Column(name = "answer")
    private String answer;

}
