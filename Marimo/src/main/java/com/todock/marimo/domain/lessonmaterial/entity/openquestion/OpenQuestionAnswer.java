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

    @Column(name = "user_id", nullable = false) // 작성한 유저 id
    private Long userId;

    @Column(name = "answer", nullable = false) // 답변
    private String answer;

    // 열린 질문 답변 생성자
    public OpenQuestionAnswer(OpenQuestion openQuestion,Long userId, String answer) {
        
        validateUserId(userId);
        validateAnswer(answer);

        this.openQuestion = openQuestion;
        this.userId = userId;
        this.answer = answer;
    }

    // 유저 id 검증
    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("유저 ID가 없습니다.");
        }
    }

    // 답변 검증
    private void validateAnswer(String answer) {
        if (answer == null) {
            throw new IllegalArgumentException("열린질문에 대한 답변이 없습니다.");
        }
    }

    // OpenQuestion와 관계 생성
    void setOpenQuestion(OpenQuestion openQuestion) {
        this.openQuestion = openQuestion;
    }

    @Override
    public String toString() {
        return "OpenQuestionAnswer{" +
                "answer='" + answer + '\'' +
                '}';
    }
}