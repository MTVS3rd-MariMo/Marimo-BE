package com.todock.marimo.domain.lessonmaterial.entity.openquestion;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tbl_open_question_answer")
public class OpenQuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openQuestionAnswerId; // 열린 질문 대답 id

    @ManyToOne(fetch = FetchType.LAZY) // 열린 질문
    @JsonIgnoreProperties
    @JoinColumn(name = "open_question_id", nullable = false)
    private OpenQuestion openQuestion;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "user_id", nullable = false) // 작성한 유저 id
    private Long userId;

    @Column(name = "answer", nullable = false) // 답변
    private String answer;

    // 열린 질문 답변 생성자
    public OpenQuestionAnswer(OpenQuestion openQuestion, Long userId, Long lessonId, String answer) {
        this.lessonId = lessonId;
        this.openQuestion = openQuestion;
        this.userId = userId;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "OpenQuestionAnswer{" +
                "answer='" + answer + '\'' +
                '}';
    }
}