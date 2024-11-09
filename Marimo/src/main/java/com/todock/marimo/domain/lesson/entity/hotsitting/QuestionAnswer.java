package com.todock.marimo.domain.lesson.entity.hotsitting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_QuestionAnswer")
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionAnswerId;

    @ManyToOne
    @JoinColumn(name = "self_introduce_id")
    private SelfIntroduce selfIntroduce; // 핫시팅에 연결된 질문-답변

    @Column(name = "contents")
    private String contents;

    @Override
    public String toString() {
        return "QuestionAnswer{" +
                "contents='" + contents + '\'' +
                '}';
    }

    public QuestionAnswer(SelfIntroduce selfIntroduce, String contents) {
        this.selfIntroduce = selfIntroduce;
        this.contents = contents;
    }
}